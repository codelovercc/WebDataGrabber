package com.cool.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by code lover on 17/3/21.
 */
@Configuration
@ComponentScan(basePackages = {
        "com.cool.config",
        "com.cool.entities",
        "com.cool.exception",
        "com.cool.file",
        "com.cool.http",
        "com.cool.log",
        "com.cool.models",
        "com.cool.repository",
        "com.cool.services",
        "com.cool.util",
        "com.cool.aop"
},
        excludeFilters = {//定义不需要扫描的Filters
        })
@PropertySources({
        @PropertySource("classpath:configDatabase.properties"),
        @PropertySource("classpath:configProject.properties")
})
@EnableTransactionManagement
/*
 *  关于启用AOP自动代理的配置，请参考 https://yq.aliyun.com/articles/38210
 *
 */
@EnableAspectJAutoProxy
/*
 * 启动任务
 */
@EnableScheduling
@EnableAsync
//@EnableCaching
public class RootConfig implements EnvironmentAware, ApplicationContextAware {
    private static final String CHARACTER_ENCODING = "UTF-8";
    public static ApplicationContext rootApplicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        rootApplicationContext = applicationContext;
    }

    @Bean(destroyMethod = "close")
    public HikariDataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(environment.getRequiredProperty("dataSource.driverClassName"));
        hikariConfig.setUsername(environment.getRequiredProperty("dataSource.user"));
        hikariConfig.setPassword(environment.getRequiredProperty("dataSource.password"));
        hikariConfig.setJdbcUrl(environment.getRequiredProperty("dataSource.jdbcUrl"));
        hikariConfig.setConnectionTestQuery(environment.getRequiredProperty("dataSource.connectionTestQuery"));
        hikariConfig.setConnectionTimeout(Long.parseLong(
                environment.getRequiredProperty("dataSource.connectionTimeout")
        ));
        hikariConfig.setIdleTimeout(Long.parseLong(
                environment.getRequiredProperty("dataSource.idleTimeout")
        ));
        hikariConfig.setMaxLifetime(Long.parseLong(
                environment.getRequiredProperty("dataSource.maxLifetime")
        ));
        hikariConfig.setMaximumPoolSize(Integer.parseInt(
                environment.getRequiredProperty("dataSource.maximumPoolSize")
        ));
        hikariConfig.setMinimumIdle(Integer.parseInt(
                environment.getRequiredProperty("dataSource.minimumIdle")
        ));
        hikariConfig.setLeakDetectionThreshold(15000);
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    @Autowired
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean sfb = new LocalSessionFactoryBean();
        sfb.setDataSource(dataSource);
        sfb.setPackagesToScan(environment.getRequiredProperty("Database.entities.packagesToScan"));
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "com.hibernate.dialect.MineSQLServer2012Dialect");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("format_sql", "true");
        properties.setProperty("hbm2ddl.auto", "update");
        sfb.setHibernateProperties(properties);
        return sfb;
    }

    /**
     * 动态添加的自动消息任务的执行器,使用这个bean 可以动态添加任务
     *
     * @return
     */
    @Bean
    public ThreadPoolTaskScheduler defaultMessageAutoSenderTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("MessageAutoSender-");
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        scheduler.setRemoveOnCancelPolicy(true);
        return scheduler;
    }

    /**
     * 动态添加的自动机器人任务的执行器,使用这个bean 可以动态添加任务
     *
     * @return
     */
    @Bean
    public ThreadPoolTaskScheduler defaultAutoRobotTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("AutoRobot-");
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        scheduler.setRemoveOnCancelPolicy(true);
        return scheduler;
    }

    /**
     * 动态添加代理定时返点的任务执行器
     *
     * @return
     */
    @Bean
    public ThreadPoolTaskScheduler defaultProxyPayBackTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("PayBack-");
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        scheduler.setRemoveOnCancelPolicy(true);
        return scheduler;
    }

    /**
     * 用于执行异步任务的执行器
     */
    @Bean
    public ThreadPoolTaskExecutor myThreadPoolTaskScheduler() {
        ThreadPoolTaskExecutor scheduler = new ThreadPoolTaskExecutor();
        scheduler.setThreadNamePrefix("ThreadTask-");
        scheduler.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        scheduler.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 30);
        scheduler.setQueueCapacity(20);
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.initialize();
        return scheduler;
    }

    /**
     * 所有启动时添加的任务都使用这个线程池
     * <br>
     * 因为注解驱动的任务，会优先使用默认的BEAN ， 在这边定义，省得他使用前面我们做其它事情的BEAN
     * <br>参考{@link org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#resolveSchedulerBean(Class, boolean)}
     * <br>默认的bean name {@link org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#DEFAULT_TASK_SCHEDULER_BEAN_NAME}
     *
     * @return
     */
    @Bean(name = ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME)
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("CoolTaskScheduler-");
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        scheduler.setRemoveOnCancelPolicy(true);
        return scheduler;
    }

    /**
     * spring MVC 异步请求时使用的执行器
     *
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor asyncRequestTaskScheduler() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 3);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("asyncRequest-");
        executor.initialize();
        return executor;
    }

    @Autowired
    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    /**
     * 设置国际化bean
     *
     * @return
     */
    @Bean
    @SuppressWarnings("all")
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource resourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("classpath:messages-info");
        resourceBundleMessageSource.setUseCodeAsDefaultMessage(true);
        resourceBundleMessageSource.setDefaultEncoding(CHARACTER_ENCODING);
        return resourceBundleMessageSource;
    }

    /**
     * 验证需要的bean
     *
     * @param messageSource 消息源
     * @return 地域化验证工厂
     */
    @Bean
    @Autowired
    public LocalValidatorFactoryBean localValidatorFactoryBean(ReloadableResourceBundleMessageSource messageSource) {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setValidationMessageSource(messageSource);
        return localValidatorFactoryBean;
    }

    @Bean
    public BeanPostProcessor persistenceTranslation() {
        return new PersistenceAnnotationBeanPostProcessor();
    }

    private Environment environment;

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
