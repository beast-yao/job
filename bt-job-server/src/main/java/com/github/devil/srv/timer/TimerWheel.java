package com.github.devil.srv.timer;

import com.github.devil.srv.core.NamedThreadFactory;
import com.github.devil.srv.core.exception.JobException;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author eric.yao
 * @date 2021/1/7
 **/
public class TimerWheel implements Timer{

    @Getter
    private long startTime;

    private HashedWheelBucket[] buckets;

    private final long tickDuration;

    private final int mask;

    private final static int DEFAULT_PRE_SIZE = 2 << 8 ;

    private final AtomicInteger state = new AtomicInteger(0);

    private final static int STATE_INIT = 0;

    private final static int STATE_START = 1;

    private final static int STATE_STOP = 3;

    private Queue<TimerOut> wait = new LinkedBlockingQueue<>();

    private Queue<TimerOut> cancel = new LinkedBlockingQueue<>();

    private CountDownLatch startInit = new CountDownLatch(1);

    private Thread workerThread;

    private Worker worker = new Worker();

    private ExecutorService executor ;

    public TimerWheel(){
        this(1,TimeUnit.NANOSECONDS,DEFAULT_PRE_SIZE);
    }

    public TimerWheel(ExecutorService executor){
        this(1,TimeUnit.NANOSECONDS,DEFAULT_PRE_SIZE,Executors.defaultThreadFactory(),executor);
    }

    public TimerWheel(long tickDuration,TimeUnit unit,int ticketPreSize){
        this(tickDuration,unit,ticketPreSize
                ,Executors.defaultThreadFactory()
                ,new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors()*4,Integer.MAX_VALUE,60,
                        TimeUnit.SECONDS,new SynchronousQueue<>(),new NamedThreadFactory("TIMER")));
    }

    /**
     * 精度精确到纳秒级别
     * @param tickDuration 指针转动一次的时间间隔
     * @param unit 时间单位
     * @param ticketPreSize 指针转动一圈的次数
     * @param threadFactory 线程factory
     * @param executor 任务执行线程池
     */
    public TimerWheel(long tickDuration,TimeUnit unit,int ticketPreSize,ThreadFactory threadFactory,ExecutorService executor) {

        if (unit == null) {
            throw new NullPointerException("unit");
        }
        if (threadFactory == null) {
            throw new NullPointerException("threadFactory");
        }
        if (tickDuration <= 0) {
            throw new IllegalArgumentException("tickDuration must be greater than 0: " + tickDuration);
        }
        if (ticketPreSize <= 0) {
            throw new IllegalArgumentException("ticksPerWheel must be greater than 0: " + ticketPreSize);
        }

        this.executor = executor;

        createBucket(ticketPreSize);
        this.tickDuration = unit.toNanos(tickDuration);

        mask = buckets.length - 1;

        workerThread = threadFactory.newThread(worker);

        if (this.tickDuration >= Long.MAX_VALUE / buckets.length) {
            throw new IllegalArgumentException(String.format(
                    "tickDuration: %d (expected: 0 < tickDuration in nanos < %d",
                    tickDuration, Long.MAX_VALUE / buckets.length));
        }
    }

    private void createBucket(int ticketPreSize){
        ticketPreSize = ticketPreSize%2 == 0?ticketPreSize:ticketPreSize+1;
        buckets = new HashedWheelBucket[ticketPreSize];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new HashedWheelBucket(this);
        }
    }

    private void start(){
        if (state.compareAndSet(STATE_INIT,STATE_START)){
            workerThread.start();
        }

        while (startTime == 0){
            try {
                // wait for workThread start end
                startInit.await();
            } catch (InterruptedException ignored) {

            }
        }
    }

    public boolean isStart(){
        return Objects.equals(state.get(),STATE_START);
    }

    @Override
    public TimerFuture delay(TimerTask task, long delay, TimeUnit unit) {
        if (task == null){
            throw new  NullPointerException("task");
        }

        if (unit == null){
            throw new  NullPointerException("unit");
        }

        if (state.get() == STATE_STOP){
            throw new JobException("TimerWheel is stoped that cannot accept the task");
        }

        /**
         * start workerThread
         */
        start();

        long deadline = System.nanoTime() + unit.toNanos(delay) - startTime;

        // Guard against overflow.
        if (delay > 0 && deadline < 0) {
            deadline = Long.MAX_VALUE;
        }

        TimerOut timerOut = new TimerOut(task, deadline,this);

        wait.add(timerOut);

        return timerOut;
    }

    @Override
    public Set<TimerFuture> stop() {
        if (state.compareAndSet(STATE_START,STATE_STOP)){
            if (workerThread.isAlive()){
                workerThread.interrupt();
                try {
                    workerThread.join(100);
                } catch (InterruptedException e) {
                }
            }
            if (executor != null && !executor.isShutdown()){
                executor.shutdown();
            }
        }else {
            return Collections.emptySet();
        }
        return worker.unprocessedTask();
    }

    private static final class HashedWheelBucket{

        private TimerWheel timerWheel;

        private Queue<TimerOut> taskQueue = new LinkedBlockingQueue<>();

        public HashedWheelBucket(TimerWheel timerWheel){
            this.timerWheel = timerWheel;
        }

        /**
         *
         * @param timerOut
         */
        public void timeOut(TimerOut timerOut){
            if (timerOut == null){
                throw new NullPointerException("timerOut");
            }

            timerOut.bucket = this;
            taskQueue.add(timerOut);

        }

        TimerOut poll(){
            return taskQueue.poll();
        }

        void remove(TimerOut timerOut){
            taskQueue.remove(timerOut);
        }

        /**
         * 从开始时间到现在的时间间隔
         * @param deadline
         */
        public void expireJob(long deadline){
            taskQueue.removeIf(timerOut -> {
                if (timerOut == null || timerOut.isDone() || timerOut.isCanceled()){
                    return true;
                }

                if (timerOut.delay > deadline){
                    return false;
                }

                if (timerOut.state.compareAndSet(TimerOut.STATE_WAIT, TimerOut.STATE_RUNNING)){
                    try {
                        if (timerWheel.executor != null){
                            timerWheel.executor.execute(timerOut.getTask());
                        }else {
                            timerOut.getTask().run();
                        }

                    }finally {
                        timerOut.state.compareAndSet(TimerOut.STATE_RUNNING, TimerOut.STATE_DONE);
                    }
                    return true;
                }
                return false;
            });
        }

    }

    private static final class TimerOut implements TimerFuture{

        private TimerTask task;

        private long delay;

        private TimerWheel wheel;

        HashedWheelBucket bucket;

        private AtomicInteger state ;

        private static final int STATE_WAIT = 0;

        private static final int STATE_CANCEL = 1;

        private static final int STATE_RUNNING = 2;

        private static final int STATE_DONE = 3;

        private TimerOut(TimerTask task,long delay,TimerWheel wheel){
            this.task = task;
            this.delay = delay;
            state = new AtomicInteger(0);
            this.wheel = wheel;
        }

        @Override
        public TimerTask getTask() {
            return task;
        }

        @Override
        public void cancel() {
            if (this.state.get() == STATE_WAIT){
                this.state.compareAndSet(STATE_WAIT,STATE_CANCEL);
                wheel.cancel.offer(this);
            }else {
                throw new IllegalStateException("task cannot be stop , state"+this.state.get());
            }
        }

        @Override
        public boolean isCanceled() {
            return this.state.get() == STATE_CANCEL;
        }

        @Override
        public boolean isDone() {
            return this.state.get() == STATE_DONE;
        }

        public void remove(){
            if (this.bucket != null) {
                this.bucket.remove(this);
            }
        }
    }

    private class Worker implements Runnable{

        private Set<TimerOut> unprocessedTask = new HashSet<>();

        /**
         * 指针次数
         */
        private long tick;

        @Override
        public void run() {
            startTime = System.nanoTime();

            if (startTime == 0){
                startTime = 1;
            }

            startInit.countDown();

            do {
                long dealTime = waitForNextTick();

                if (dealTime > 0) {
                    int idx = (int) (tick & mask);
                    /**
                     * 清除取消的任务
                     */
                    processCancelTask();
                    /**
                     * 将任务从队列中推送至bucket中
                     */
                    transferTimeoutsToBuckets();
                    HashedWheelBucket bucket = buckets[idx];
                    bucket.expireJob(dealTime);
                }
                tick ++;
            }while (state.get() == STATE_START);

            for (HashedWheelBucket bucket : buckets) {
                TimerOut timerOut = bucket.poll();
                if (timerOut != null && timerOut.state.get() == TimerOut.STATE_WAIT){
                    unprocessedTask.add(timerOut);
                }
            }

            for (TimerOut timerOut : wait) {
                if (timerOut.state.get() == TimerOut.STATE_WAIT){
                    unprocessedTask.add(timerOut);
                }
            }
            processCancelTask();
        }

        /**
         * calculate goal nanoTime from startTime and current tick number,
         * then wait until that goal has been reached.
         * @return Long.MIN_VALUE if received a shutdown request,
         * current time otherwise (with Long.MIN_VALUE changed by +1)
         */
        private long waitForNextTick() {
            long deadline = tickDuration * (tick + 1);

            for (;;) {
                final long currentTime = System.nanoTime() - startTime;
                long sleepTimeMs = (deadline - currentTime)/1000000;

                if (sleepTimeMs <= 0) {
                    if (currentTime == Long.MIN_VALUE) {
                        return -Long.MAX_VALUE;
                    } else {
                        return currentTime;
                    }
                }

                try {
                    Thread.sleep(sleepTimeMs);
                } catch (InterruptedException ignored) {
                    if (state.get() == STATE_STOP) {
                        return Long.MIN_VALUE;
                    }
                }
            }
        }

        private void transferTimeoutsToBuckets() {
            for (int i = 0; i < 100000; i++) {
                TimerOut timerOut = wait.poll();
                /**
                 * no task
                 */
                if (timerOut == null){
                    break;
                }

                if (timerOut.isCanceled()){
                    continue;
                }

                /**
                 * 从开始时间到现在指针一共需要走的次数
                 */
                long allTickets = timerOut.delay/tickDuration;

                /**
                 * 获取bucket位置，不执行过期任务
                 */
                int idx = (int)(Math.max(allTickets,tick)&mask);

                HashedWheelBucket bucket = buckets[idx];

                bucket.timeOut(timerOut);
            }
        }

        private void processCancelTask(){
            TimerOut timerOut = null;
            while ((timerOut = cancel.poll()) != null){
                timerOut.remove();
            }
        }

        private Set<TimerFuture> unprocessedTask(){
            return Collections.unmodifiableSet(unprocessedTask);
        }
    }

}
