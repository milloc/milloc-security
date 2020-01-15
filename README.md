# milloc-security
## 概要
自定义注解，通过反射，实现对controller和controller的handler的权限控制
## 原理
- 通过注解标记，获取需要权限认证的controller和handler
- 数据库保存权限，精确到某个handler或者controller
- 通过拦截器，在controller层进行请求的时候，进行权限匹配
## 架构
springboot + web + jpa
## 核心代码
### 权限校验标记注解
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface RightControl {
    }
### 通过反射获取需要权限认证的类和方法
    public class RightControlServiceImpl implements RightControlService {
        private static final String CLASS_SUFFIX = ".class";
        private static final String CLASS_SUFFIX_PATTERN = "\\.class$";
        private static final String PACKAGE_NAME = "com.milloc.security";
        private Set<String> rightControls;
    
        @Override
        public Set<String> listRightControls() {
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
### 拦截器
    @Log4j2
    public class RightControlInterceptor implements HandlerInterceptor {
        ...  
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            if (!(handler instanceof HandlerMethod)) {
                return true;
            }
    
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Class<?> controller = handlerMethod.getBeanType();
            CurrentUser currentUser = userService.getCurrentUser();
    
            if (controller.getAnnotation(RightControl.class) != null) {
                if (!currentUser.getRights().contains(controller.toString())) {
                    unAuthorization(currentUser, handlerMethod.getMethod());
                    return false;
                }
            }
    
            if (handlerMethod.getMethodAnnotation(RightControl.class) != null) {
                if (!currentUser.getRights().contains(handlerMethod.getMethod().toString())) {
                    unAuthorization(currentUser, handlerMethod.getMethod());
                    return false;
                }
            }
    
            return true;
        }
    
        @SneakyThrows
        private void unAuthorization(CurrentUser user, Method method) {
            log.debug("没有权限 user = {}, method = {}", objectMapper.writeValueAsString(user), method.toString());
            throw new UnAuthorizationException(user, method);
        }
    }