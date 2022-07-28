package com.l.rpc.spring;

import com.l.rpc.annotations.EnableRpcClient;
import com.l.rpc.annotations.RPCClient;
import com.l.rpc.constants.RpcConstants;
import com.l.rpc.factory.RpcFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RpcInitRegistrar
 * @Description: ZrpcClient注解注入处理
 * @date 2022/7/4 3:08 PM
 */
public class RpcInitRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware, BeanFactoryAware {

    private static final Logger log = LoggerFactory.getLogger(RpcInitRegistrar.class);

    private Environment environment;
    private ResourceLoader resourceLoader;
    private BeanFactory beanFactory;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        //必须在启动类上开启
        if (!annotationMetadata.hasAnnotation(EnableRpcClient.class.getName())) {
            return;
        }
        // useDefaultFilters = false,即第二个参数 表示不扫描 @Component、@ManagedBean、@Named 注解标注的类
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        // 添加我们自定义注解的扫描
        scanner.addIncludeFilter(new AnnotationTypeFilter(RPCClient.class));
        // 扫描包
        for (String needScanPackage : RpcConstants.BASE_PACKAGES) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(needScanPackage);
            try {
                registerCandidateComponents(registry, candidateComponents);
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage(), e);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 注册 BeanDefinition
     */
    private void registerCandidateComponents(BeanDefinitionRegistry registry, Set<BeanDefinition> candidateComponents) throws Throwable {
        for (BeanDefinition candidateComponent : candidateComponents) {
            if (candidateComponent instanceof AnnotatedBeanDefinition) {
                registerBeans((AnnotatedBeanDefinition) candidateComponent);
            }
        }
    }

    /**
     * 反射拿到class，创建动态代理，并动态注册到容器中
     *
     * @param annotatedBeanDefinition
     */
    private void registerBeans(AnnotatedBeanDefinition annotatedBeanDefinition) throws Throwable {
        String className = annotatedBeanDefinition.getBeanClassName();
        AnnotationMetadata annotationMetadata = annotatedBeanDefinition.getMetadata();
        Class<?> target = Class.forName(annotationMetadata.getClassName());
        try {
            if (null == this.beanFactory.getBean(target)) {
                ((DefaultListableBeanFactory) this.beanFactory).registerSingleton(className, RpcFactory.getInstance().getService(target));
            }
        } catch (NoSuchBeanDefinitionException e) {
            ((DefaultListableBeanFactory) this.beanFactory).registerSingleton(className, RpcFactory.getInstance().getService(target));
        }
    }

    /**
     * 自定义扫描器
     *
     * @return
     */
    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}