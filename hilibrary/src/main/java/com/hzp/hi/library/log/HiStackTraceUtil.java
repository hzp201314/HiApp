package com.hzp.hi.library.log;

/**
 * 堆栈信息工具类
 */
public class HiStackTraceUtil {
    public static StackTraceElement[] getCroppedRealStackTrace(StackTraceElement[] stackTrace, String ignorePackage, int maxDepth) {
         return cropStackTrace(getRealStackTrace(stackTrace, ignorePackage),maxDepth);
    }

    /**
     * 获取除忽略包名之外的堆栈信息
     *
     * @param stackTrace    堆栈信息
     * @param ignorePackage 忽略包名
     * @return 指定的堆栈信息
     */
    private static StackTraceElement[] getRealStackTrace(StackTraceElement[] stackTrace, String ignorePackage) {
        int ignoreDepth = 0;
        int allDepth = stackTrace.length;
        String className;
        for (int i = allDepth - 1; i >= 0; i--) {
            className = stackTrace[i].getClassName();
            if (ignorePackage != null && className.startsWith(ignorePackage)) {
                ignoreDepth = i + 1;
                break;
            }
        }

        int realDepth = allDepth - ignoreDepth;
        StackTraceElement[] realStack = new StackTraceElement[realDepth];
        System.arraycopy(stackTrace, ignoreDepth, realStack, 0, realDepth);
        return realStack;
    }

    /**
     * 裁剪堆栈信息
     *
     * @param callStack 堆栈信息
     * @param maxDepth  最大深度
     * @return 最大深度的堆栈信息
     */
    private static StackTraceElement[] cropStackTrace(StackTraceElement[] callStack, int maxDepth) {
        int realDepth = callStack.length;
        if (maxDepth > 0) {
            realDepth = Math.min(maxDepth, realDepth);
        }
        StackTraceElement[] realStack = new StackTraceElement[realDepth];
        System.arraycopy(callStack, 0, realStack, 0, realDepth);
        return realStack;
    }
}
