package com.milloc.security.service.impl;

import com.milloc.security.annotation.RightControl;
import com.milloc.security.service.RightControlService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author gongdeming
 * @date 2020-01-15
 */
@Service
@Log4j2
public class RightControlServiceImpl implements RightControlService {
    private static final String CLASS_SUFFIX = ".class";
    private static final String CLASS_SUFFIX_PATTERN = "\\.class$";
    private static final String PACKAGE_NAME = "com.milloc.security";
    private Set<String> rightControls;

    @Override
    public synchronized Set<String> listRightControls() {
        if (rightControls == null) {
            scanRightControls();
            return rightControls;
        }
        return rightControls;
    }

    @SneakyThrows
    private void scanRightControls() {
        URL url = this.getClass().getClassLoader().getResource(PACKAGE_NAME.replace(".", File.separator));
        Objects.requireNonNull(url);
        Path rootPath = Paths.get(url.toURI());
        File file = rootPath.toFile();
        // 通过文件获得类名
        log.debug("扫描类文件开始");
        Set<Path> clazzPaths = scanClazzFile(file);
        log.debug("扫描类文件结束");
        // 全限定的类名
        List<String> clazzNames = clazzPaths.stream()
                .map(rootPath::relativize)
                .map(Path::toString)
                .map(f -> PACKAGE_NAME + "." + f.replaceAll(CLASS_SUFFIX_PATTERN, "").replace(File.separator, "."))
                .collect(Collectors.toList());
        log.debug("类文件转化结果 {}", clazzNames);
        // 全限定类名转化成Class对象
        List<Class> clazzLists = new ArrayList<>();
        for (String clazzName : clazzNames) {
            try {
                clazzLists.add(Class.forName(clazzName));
            } catch (Exception e) {
                log.error("加载类失败 {}", clazzName, e);
            }
        }
        log.debug("类文件加载结果 {}", clazzLists);
        // 筛选注解标注
        rightControls = clazzLists.stream().flatMap(c -> {
            Annotation classAnnotation = c.getAnnotation(RightControl.class);
            Stream.Builder<String> builder = Stream.builder();
            if (classAnnotation != null) {
                builder.add(c.toString());
            }
            Method[] methods = c.getMethods();
            if (methods.length > 0) {
                for (Method method : methods) {
                    RightControl annotation = AnnotationUtils.getAnnotation(method, RightControl.class);
                    if (annotation != null) {
                        builder.add(method.toString());
                    }
                }
            }
            return builder.build();
        }).collect(Collectors.toSet());
        log.debug("权限控制的类或方法 {}", rightControls);
    }

    private Set<Path> scanClazzFile(File file) {
        log.debug("扫描文件 {}", file.toPath());
        if (file.isFile()) {
            if (file.getName().endsWith(CLASS_SUFFIX)) {
                return Collections.singleton(file.toPath());
            }
            return Collections.emptySet();
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            Set<Path> res = new HashSet<>();
            if (files != null && files.length > 0) {
                for (File value : files) {
                    res.addAll(scanClazzFile(value));
                }
            }
            return res;
        }
        return Collections.emptySet();
    }
}
