package com.ydlclass.aspect;

import com.ydlclass.annotation.Log;
import com.ydlclass.core.RedisTemplate;
import com.ydlclass.entity.YdlOperLog;
import com.ydlclass.service.YdlOperLogService;
import com.ydlclass.util.AuthUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
@Aspect
public class LogAspect implements BeanFactoryAware {

    @Resource
    private YdlOperLogService ydlOperLogService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private HttpServletRequest request;

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    // 代码执行成功了
    @AfterReturning("@annotation(log)")
    public void afterReturning(JoinPoint joinPoint, Log log) {
        System.out.println("log-----" + Thread.currentThread().getName());
        YdlOperLog ydlOperLog = creatOper(joinPoint, log, null);

        LogAspect logAspect = beanFactory.getBean(this.getClass());
        logAspect.logHandler(ydlOperLog);
    }

    //
    @AfterThrowing(value = "@annotation(log)", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, Log log, Exception exception) {
        System.out.println("log-----" + Thread.currentThread().getName());
        YdlOperLog ydlOperLog = creatOper(joinPoint, log, exception);

        // 通过 bean 工厂创建 bean 拿到经过增强代理后的 bean 再去调用增强方法
        LogAspect logAspect = beanFactory.getBean(this.getClass());
        logAspect.logHandler(ydlOperLog);
    }

    // 自己实现的 异步日志
//    /**
//     * 真正执行异步日志的方法
//     *
//     * @param joinPoint
//     * @param request
//     * @param log
//     * @param exception
//     */
//    private void logHandler(JoinPoint joinPoint, HttpServletRequest request, Log log, Exception exception) {
//
//        // 每次请求都会创建一个线程池
////        ExecutorService executorService = Executors.newFixedThreadPool(10);
////        executorService.execute(() -> {
////
////        });
//
//        System.out.println("log-----" + Thread.currentThread().getName());
//
//        // 提交任务
//
//        System.out.println("log-----" + Thread.currentThread().getName());
//        // 根据现场信息, 封装日志实列
//        YdlOperLog ydlOperLog = new YdlOperLog();
//        ydlOperLog.setTitle(log.title());
//        ydlOperLog.setBusinessType(log.businessType());
//        ydlOperLog.setStatus(200);
//        if (exception != null) {
//            ydlOperLog.setErrormsg(exception.getMessage().length() > 1000 ?
//                    exception.getMessage().substring(0, 1000) : exception.getMessage());
//            ydlOperLog.setStatus(500);
//        }
//
//
//        // 从request 获取
//        ydlOperLog.setOperIp(request.getRemoteAddr());
//        ydlOperLog.setRequestMethod(request.getMethod());
//        ydlOperLog.setOperUrl(request.getRequestURI());
//        ydlOperLog.setOpertime(new Date());
//
//        // 要注意空指针的问题
//        if (AuthUtil.getLoginUser(redisTemplate, request) != null
//                && AuthUtil.getLoginUser(redisTemplate, request).getYdlUser() != null) {
//
//            ydlOperLog.setOperName(AuthUtil
//                    .getLoginUser(redisTemplate, request)
//                    .getYdlUser()
//                    .getUserName());
//        }
//
//        ydlOperLog.setMethod(joinPoint.getSignature().getName());
//        executorService.execute(() -> {
//            // 保存日志对象
//            ydlOperLogService.insert(ydlOperLog);
//        });
//    }

    @Async("ydl_log")
    public void logHandler(YdlOperLog ydlOperLog) {
        System.out.println("log-----" + Thread.currentThread().getName());
        // 保存日志对象
        ydlOperLogService.insert(ydlOperLog);
    }

    private YdlOperLog creatOper(JoinPoint joinPoint, Log log, Exception exception) {

        // 根据现场信息, 封装日志实列
        YdlOperLog ydlOperLog = new YdlOperLog();
        ydlOperLog.setTitle(log.title());
        ydlOperLog.setBusinessType(log.businessType());
        ydlOperLog.setStatus(200);
        if (exception != null) {
            ydlOperLog.setErrormsg(exception.getMessage().length() > 1000 ?
                    exception.getMessage().substring(0, 1000) : exception.getMessage());
            ydlOperLog.setStatus(500);
        }

        // 从request 获取
        ydlOperLog.setOperIp(request.getRemoteAddr());
        ydlOperLog.setRequestMethod(request.getMethod());
        ydlOperLog.setOperUrl(request.getRequestURI());
        ydlOperLog.setOpertime(new Date());

        // 要注意空指针的问题
        if (AuthUtil.getLoginUser(redisTemplate, request) != null
                && AuthUtil.getLoginUser(redisTemplate, request).getYdlUser() != null) {

            ydlOperLog.setOperName(AuthUtil
                    .getLoginUser(redisTemplate, request)
                    .getYdlUser()
                    .getUserName());
        }

        ydlOperLog.setMethod(joinPoint.getSignature().getName());
        return ydlOperLog;
    }


}
