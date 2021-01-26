package com.github.devil.srv.banner;

import com.github.devil.srv.util.Utils;
import lombok.SneakyThrows;
import org.springframework.boot.Banner;
import org.springframework.boot.ResourceBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * @author eric.yao
 * @date 2021/1/22
 **/
public class JobBanner implements Banner {

    private static final String JOB_SRV = " :: JOB SRV :: ";

    @SneakyThrows
    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        Banner resource = getResourceLoader(environment);
        if (resource != null){
            String banner = createStringFromBanner(resource,environment,sourceClass);
            out.println(banner);
            out.println(AnsiOutput.toString(AnsiColor.GREEN, JOB_SRV, AnsiColor.DEFAULT, "                       ",
                    AnsiStyle.FAINT, getVersion()));
            out.println();
            out.flush();
        }
    }

    private Banner getResourceLoader(Environment environment) {
        String location = environment.getProperty(SpringApplication.BANNER_LOCATION_PROPERTY, "job-banner.txt");
        Resource resource = new DefaultResourceLoader().getResource(location);
        if (resource.exists()) {
            return new ResourceBanner(resource);
        }
        return null;
    }

    private String createStringFromBanner(Banner banner, Environment environment, Class<?> mainApplicationClass)
            throws UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        banner.printBanner(environment, mainApplicationClass, new PrintStream(baos));
        String charset = environment.getProperty("spring.banner.charset", "UTF-8");
        return baos.toString(charset);
    }

    private String getVersion(){
        String version = Utils.getVersion();
        if (version != null){
            return "(v"+version+")";
        }
        return "???";
    }
}
