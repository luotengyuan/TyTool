package com.lois.tytool.base.math;

import com.lois.tytool.base.string.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @Description 数学工具类，提供四则表达式公式计算、加、减、乘、除计算
 * @Author Luo.T.Y
 * @Date 2022/1/21 16:09
 */
public class MathUtils {

    /**
     * 左括号
     */
    private final static String LEFT_PARENTHESIS = "(";
    /**
     * 右括号
     */
    private final static char RIGHT_PARENTHESIS_CHAR = ')';
    /**
     * 左括号
     */
    private final static char LEFT_PARENTHESIS_CHAR = '(';
    /**
     * 空字符
     */
    private final static char EMPTY_CHAR = ' ';
    /**
     * 加法字符
     */
    private final static char ADD_CHAR = '+';
    /**
     * 除法字符
     */
    private final static char DIV_CHAR = '/';
    /**
     * 乘法字符
     */
    private final static char MUL_CHAR = '*';
    /**
     * 减法字符
     */
    private final static char SUB_CHAR = '-';

    /**
     * 加法字符
     */
    private final static String ADD_STR = "+";
    /**
     * 除法字符
     */
    private final static String DIV_STR = "/";
    /**
     * 乘法字符
     */
    private final static String MUL_STR = "*";
    /**
     * 减法字符
     */
    private final  static String SUB_STR = "-";

    /**
     * 默认保留小数点位数精度
     */
    private final static int DEF_DIV_SCALE = 20;

    /**
     * 左花括号
     */
    private final static char LEFT_CURLY_BRACES = '{';
    /**
     * 右花括号
     */
    private final static char RIGHT_CURLY_BRACES = '}';

    /**
     * 提供精确的加法运算。
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static String add(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2).toPlainString();
    }

    /**
     * 提供精确的减法运算。
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static String sub(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.subtract(b2).toPlainString();
    }

    /**
     * 提供精确的乘法运算。
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static String mul(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2).toPlainString();
    }
    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static String div(String v1, String v2) {
        return div(v1, v2, DEF_DIV_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 除数
     * @param v2 被除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static String div(String v1, String v2, int scale) {
        if(scale < 0) {
            throw new IllegalArgumentException("精度位数不能小于零");
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    /**
     * 提供精确的小数位四舍五入处理。
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static String round(String v, int scale) {
        if(scale < 0) {
            throw new IllegalArgumentException("精度位数不能小于零");
        }
        return div(v, "1", scale);
    }

    /**
     * 格式化数字格式
     * @param pattern 格式
     * @param value 数字
     * @return 格式后的数据
     */
    public static String format(String pattern, double value) {
        if (StringUtils.isBlank(pattern)) {
            throw new NumberFormatException("格式化公式不能为空");
        }
        //实例化对象，传入模板
        DecimalFormat df = new DecimalFormat(pattern);
        //格式化数字
        return df.format(value);
    }

    /**
     * 将字符串数字进行分组，添加,号
     * 即千分位加逗号进行分组显示
     * 例如：55555 格式化为：55,555
     * 6666.66格式化为 6,666.66
     * @param numberValue 待格式数字
     * @return 分组格式后的数字
     */
    public static String formatGrouping(String numberValue) {
        if (StringUtils.isBlank(numberValue)) {
            throw new NumberFormatException("格式化公式不能为空");
        }
        //分割号
        String segmentation = ",";
        if (numberValue.contains(segmentation)) {
            numberValue = numberValue.replace(segmentation, "");
        }
        boolean isNegativeNumber = false;
        if (numberValue.startsWith("-")) {
            isNegativeNumber = true;
            numberValue = numberValue.substring(1);
        }
        // 需要添加逗号的字符串（整数）
        String addCommaStr = null;
        // 小数，等逗号添加完后，最后在末尾补上
        String tmpCommaStr = "";
        // 小数点
        String decimalPoint = ".";
        if (numberValue.contains(decimalPoint)) {
            int index = numberValue.indexOf(".");
            addCommaStr = numberValue.substring(0, index);
            tmpCommaStr = numberValue.substring(index);
        } else{
            addCommaStr = numberValue;
        }
        //数字分组大小，按此大小进行分割，每组加上分割号
        int groupingDigits = 3;
        int length = addCommaStr.length();
        //容量等于原始字符串容量 + ,逗号的数量
        int capacity = length + length/groupingDigits;
        StringBuilder builder = new StringBuilder(capacity);
        char[] chars = addCommaStr.toCharArray();
        int count = 0;

        for (int i = length -1; i >= 0; i--) {
            char c = chars[i];
            //达到每组的大小时，则加上分割符
            if (count != 0 && count % groupingDigits == 0) {
                builder.append(segmentation);
            }
            builder.append(c);
            count ++;
        }
        if (isNegativeNumber) {
            builder.append("-");
        }
        //将结果进行反转
        return builder.reverse().append(tmpCommaStr).toString();
    }

    /**
     * 将数字后面的0去掉
     * @param number 待忽略0的数字
     * @return 忽略0后的数字
     */
    public static String removeZero(String number) {
        if (StringUtils.isBlank(number)) {
            return number;
        }
        StringBuilder builder = new StringBuilder();
        char[] cs = number.toCharArray();
        boolean isIgnore = true;
        for (int i = cs.length - 1; i >= 0; i--) {
            char c = cs[i];
            if (c == ' ') {
                continue;
            }
            if (c == '0') {
                if (!isIgnore) {
                    builder.append(c);
                }
            } else {
                isIgnore = false;
                if (c == '.' && builder.toString().isEmpty()) {
                    continue;
                }
                builder.append(c);
            }
        }
        return builder.reverse().toString();
    }

    /**
     * 求和
     * @param numbers 数据
     * @return 累计求和数
     */
    public static String sum(String... numbers) {
        if (numbers == null || numbers.length <= 0) {
            return "0";
        }
        //只有一个数据
        if (numbers.length == 1) {
            return numbers[0];
        }
        String sumNumber = "0";
        for (String number : numbers) {
            sumNumber = add(sumNumber, number);
        }
        return sumNumber;
    }

    /**
     * 计算平均数；
     * 默认保留两位小数
     * @param numbers 数据
     * @return 平均数
     */
    public static String average(String... numbers) {
        return average(2, numbers);
    }

    /**
     * 计算平均数
     * @param scale 保留小数位
     * @param numbers 数据
     * @return 平均数
     */
    public static String average(int scale, String... numbers) {
        if (numbers == null || numbers.length <= 0) {
            return "0";
        }
        String sum = sum(numbers);
        return div(sum, String.valueOf(numbers.length), scale);
    }

    /**
     * 财务净现值计算
     * @param rate 收益率
     * @param values ci-co 资金流
     * @param scale 保留小数位数
     * @return 净现值
     */
    public static String npv(double rate, int scale, String... values){
        String npv = "0";
        for(int i = 0; i < values.length; i++) {
            String aDouble = values[i];
            double pow = pow(1 + rate, (i + 1));
            String div = div(aDouble, Double.toString(pow));
            npv = add(npv, div);
        }
        return round(npv, scale);
    }
    /**
     * 财务净现值计算
     * 默认保留小数后两位，四舍五入
     * @param rate 收益率
     * @param values ci-co 资金流
     * @return 净现值
     */
    public static String npv(double rate, String... values){
        int scale = 2;
        return npv(rate, scale, values);
    }

    /**
     * 内部收益率（IRR）计算
     * 默认保留小数点后四位；
     * 估计结果默认10%
     * @param cashFlows 现金流
     * @return 内部收益率
     */
    public static double irr(String... cashFlows) {
        return irr(0, cashFlows);
    }

    /**
     * 内部收益率（IRR）计算
     * 默认保留小数点后四位；
     * 估计结果为0时，默认等于0.1；小于0时，则默认等于0.5
     * @param estimatedResult 估计结果
     * @param cashFlows 现金流
     * @return 内部收益率
     */
    public static double irr(double estimatedResult, String... cashFlows) {
        //保留小数点后四位
        int scale = 4;
        return irr(estimatedResult, scale, cashFlows);
    }

    /**
     * 内部收益率（IRR）计算
     * 默认保留小数点后四位；
     * 估计结果为0时，默认等于0.1；小于0时，则默认等于0.5
     * @param estimatedResult 估计结果
     * @param scale 保留小数点位数
     * @param cashFlows 现金流
     * @return 内部收益率
     */
    public static double irr(double estimatedResult, int scale, String... cashFlows) {
        double result = 0;
        if (cashFlows == null || cashFlows.length <= 0) {
            throw new IllegalArgumentException("现金流数据不能为空");
        }
        // check if business startup costs is not zero:
        if (Double.parseDouble(cashFlows[0]) >=0) {
            throw new IllegalArgumentException("初始现金流不能大于等于0");
        }
        int noOfCashFlows = cashFlows.length;
        String sumCashFlows = "0";
        // check if at least 1 positive and 1 negative cash flow exists:
        int noOfNegativeCashFlows = 0;
        int noOfPositiveCashFlows = 0;
        for (String cashFlow : cashFlows) {
            sumCashFlows = add(sumCashFlows, cashFlow);
            if (Double.parseDouble(cashFlow) > 0) {
                noOfPositiveCashFlows++;
            } else {
                if (Double.parseDouble(cashFlow) < 0) {
                    noOfNegativeCashFlows++;
                }
            }
        }

        // at least 1 negative and 1 positive cash flow available?
        if (!(noOfNegativeCashFlows > 0 && noOfPositiveCashFlows > 0)) {
            return result;
        }
        // set estimated result:
        // default: 10%
        double irrGuess = getIrrGuess(estimatedResult);
        // initialize first IRR with estimated result:
        double irr = getIrr(sumCashFlows, irrGuess);
        // iteration:
        // the smaller the distance, the smaller the interpolation
        // error
        double minDistance = 1e-15;

        // business startup costs
        String cashFlowStart = cashFlows[0];
        // 最大的迭代次数
        int maxIteration = 100;
        boolean wasHi = false;
        String cashValue;
        for (int i = 0; i <= maxIteration; i++) {
            // calculate cash value with current irr:
            // init with startup costs
            cashValue = cashFlowStart;

            // for each cash flow
            for (int j = 1; j < noOfCashFlows; j++) {
                String v1 = cashFlows[j];
                String v2 = String.valueOf(pow(1 + irr, j));
                String div = div(v1, v2);
                cashValue = add(cashValue, div);
            }

            // cash value is nearly zero
            if (Math.abs(Double.parseDouble(cashValue)) < 0.01) {
                result = irr;
                break;
            }

            // adjust irr for next iteration:
            // cash value > 0 => next irr > current irr
            if (Double.parseDouble(cashValue) > 0) {
                if (wasHi) {
                    irrGuess /= 2;
                }
                irr += irrGuess;
                if (wasHi) {
                    irrGuess -= minDistance;
                    wasHi = false;
                }
            } else {
                // cash value < 0 => next irr < current irr
                irrGuess /= 2;
                irr -= irrGuess;
                wasHi = true;
            }
            // estimated result too small to continue => end
            // calculation
            if (irrGuess <= minDistance) {
                result = irr;
                break;
            }
        }
        String value = round(String.valueOf(result), scale);
        return Double.parseDouble(value);
    }

    private static double getIrrGuess(double estimatedResult) {
        double irrGuess = 0.1;
        if (estimatedResult != 0) {
            irrGuess = estimatedResult;
            if (irrGuess <= 0) {
                irrGuess = 0.5;
            }
        }
        return irrGuess;
    }

    private static double getIrr(String sumCashFlows, double irrGuess) {
        double irr;
        if (Double.parseDouble(sumCashFlows) < 0) {
            // sum of cash flows negative?
            irr = -irrGuess;
        } else {
            // sum of cash flows not negative
            irr = irrGuess;
        }
        return irr;
    }

    /**
     * 指数运算
     * 例如：2的2次幂运算
     * @param v1 需要进行指数运算的数值
     * @param v2 指数
     * @return 指数运算后的值
     */
    public static double pow(double v1, double v2) {
        return Math.pow(v1, v2);
    }

    /**
     *  执行数学四则运算公式。
     *  传入字符串四则运算公式，自动计算结果。（当有除不尽的小数时，默认四舍五入，并保留10位小数点）
     * @param formula 四则运算公式（支持加、减、乘、除、括号运算）
     * @return 公式执行结果
     */
    public static String executeFormula(String formula) {
        return  executeFormula(formula, DEF_DIV_SCALE);
    }

    /**
     *  执行数学四则运算公式。
     *  传入字符串四则运算公式，自动计算结果。
     * @param formula 四则运算公式（支持加、减、乘、除、括号运算）
     * @param scale 保留小数位数（当有除不尽的数据时，保留此小数点数据，其它的四舍五入）
     * @return 公式执行结果
     */
    public static String executeFormula(String formula, int scale) {
        return executeFormula(formula, scale, null);
    }
    /**
     *  执行数学四则运算公式。
     *  支持占位符，通过listener接口，将占位通各并返回真实的数据。占位符使用{}表示，例{a}*{b}
     *  传入字符串四则运算公式，自动计算结果。（当有除不尽的小数时，默认四舍五入，并保留10位小数点）
     * @param formula 四则运算公式（支持加、减、乘、除、括号运算）
     * @param listener 属性监听，公式中有变量时，需要传入该listener
     * @return 公式执行结果
     */
    public static String executeFormula(String formula, MathListener listener) {
        return  executeFormula(formula, DEF_DIV_SCALE, listener);
    }
    /**
     *  执行数学四则运算公式。
     *  支持占位符，通过listener接口，将占位通各并返回真实的数据。占位符使用{}表示，例{a}*{b}
     *  传入字符串四则运算公式，自动计算结果。
     * @param formula 四则运算公式（支持加、减、乘、除、括号运算）
     * @param scale 保留小数位数（当有除不尽的数据时，保留此小数点数据，其它的四舍五入）
     * @param listener 属性监听，公式中有变量时，需要传入该listener
     * @return 公式执行结果
     */
    public static String executeFormula(String formula, int scale, MathListener listener) {
        if (scale < 0) {
            scale = DEF_DIV_SCALE;
        }
        //有括号运算，先去除括号
        if (formula.contains(LEFT_PARENTHESIS)) {
            formula = parsingBrackets(formula, DEF_DIV_SCALE, listener);
        }
        String value = parsingFormula(formula, DEF_DIV_SCALE, listener);
        return round(value, scale);
    }
    /**
     * 简化四则表达式括号，即去括号运算。
     * @param formula 有括号的四则运算公式
     * @param scale 保留小数位数（当有除不尽的数据时，保留此小数点数据，其它的四舍五入）
     * @param listener 属性监听，公式中有变量时，需要传入该listener
     * @return 去掉括号简化后的公式
     */
    private static String parsingBrackets(String formula, int scale, MathListener listener) {

        char[] chars = formula.toCharArray();
        StringBuilder builder = new StringBuilder(8);
        //记录上一个节点数
        Node previous = null;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            //如果有空字符串，直接忽略
            if (c == EMPTY_CHAR) {
                continue;
            }
            if (c == LEFT_PARENTHESIS_CHAR) {
                //取出当前运算符号
                String cs = "" + c;
                //运算符号节点
                Node symbol = buildNode(cs, null, null);
                //如果开头就是括号，则跳过，并把上一个字点置为当前节点
                if (i == 0) {
                    previous = symbol;
                    continue;
                }
                //如果连续为括号，则两个括号中间没有值，则置上一个节点为当前节点后跳过
                if ("".equals(builder.toString())) {
                    if (previous != null) {
                        previous.setNext(symbol);
                    }
                    symbol.setPrevious(previous);
                    previous = symbol;
                    continue;
                }
                //来到这里代表括号与括号之间有数据
                //普通内容节点 设置数值的下一个节点为运算符号
                Node number = buildNode(builder.toString(), symbol, null);
                if (previous != null) {
                    //如果上一个节点不为空，则设置数值的上一个节点为number
                    previous.setNext(number);
                    number.setPrevious(previous);
                }
                //设置运算符号的上一个节点为数值
                symbol.setPrevious(number);
                //设置上一个节点为运算符号
                previous = symbol;
                //重置builder
                builder = new StringBuilder(8);
            } else if (c == RIGHT_PARENTHESIS_CHAR) {
                String s1 = builder.toString();
                Node tmp = previous;
                //判断上一个节点数据内容是否为左括号（，如果不是，则代表中间有多段数据，需要进行合并
                if (!tmp.getValue().equals(LEFT_PARENTHESIS)) {
                    //链路查询，直到查询到左括号
                    for (;;) {
                        String value = tmp.getValue().toString();
                        if (LEFT_PARENTHESIS.equals(value)) {
                            previous = tmp;
                            break;
                        } else {
                            if (tmp.getPrevious() == null) {
                                break;
                            }
                            tmp = tmp.getPrevious();
                        }
                    }
                    tmp = tmp.getNext();
                    //从左括号下一个点节开始合并数据，循环合并，直到最后一个数据节点
                    StringBuilder b = getNodeLinkValue(tmp);
                    s1 = b.append(s1).toString();
                }
                //合并后的公式，进行四则运算
                String cal = parsingFormula(s1, scale, listener);
                //如果当前节点没有上一个节点，代表该括号为开始节点，则重置数据
                if (previous.getPrevious() == null) {
                    previous = buildNode(cal, null, null);
                    builder = new StringBuilder();
                } else {
                    //去括号，将左括号的上一个节点的上一个节点，置为上一个节点记录
                    previous = previous.getPrevious();
                    previous.setNext(null);
                    builder = new StringBuilder();
                    builder.append(cal);
                }
            } else {
                builder.append(c);
            }
            //如果是最后一个，则添加数值
            if (i == chars.length - 1) {
                Node number = buildNode(builder.toString(), null, previous);
                //判断上一个节点是否有数据，因为有可能该公式为非法公式，
                if (previous == null) {
                    previous = number;
                }
                previous.setNext(number);
            }
        }
        Node firstNode = null;
        String f = null;
        if (previous != null) {
            firstNode = getFirstNode(previous);
            f = getNodeLinkValue(firstNode).toString();
        }
        //将去括号后的链路进行公式合并
        //返回去括号的公式
        return f;
    }

    /**
     * 解析四则运算公式，将公式运算符号及数据进行公离。
     * @param formula 四则运算公式
     * @param scale 保留小数位数（当有除不尽的数据时，保留此小数点数据，其它的四舍五入）
     * @param listener 属性监听，公式中有变量时，需要传入该listener
     * @return 执行公式结果
     */
    private static String parsingFormula(String formula, int scale, MathListener listener) {

        char[] chars = formula.toCharArray();

        Node first = null;
        StringBuilder builder = new StringBuilder(8);
        //记录上一个节点数
        Node previous = null;
        //记录是否有乘除法运算
        boolean ride = false;
        //记录是否有加减法运算
        boolean add = false;
        StringBuilder property = new StringBuilder(8);
        boolean start = false;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            //如果有空字符串，直接忽略
            if (c == EMPTY_CHAR) {
                continue;
            }
            //字符串是运算符号
            if (c == ADD_CHAR || c == SUB_CHAR || c == MUL_CHAR || c == DIV_CHAR) {
                if (c == ADD_CHAR || c == SUB_CHAR) {
                    add = true;
                }
                if (c == MUL_CHAR || c == DIV_CHAR) {
                    ride = true;
                }
                //判断是否是减号
                if (c == SUB_CHAR) {
                    //如果是减号，需要判断是否是负号
                    //判断前一个字符是不是运算符号或者为空
                    boolean isMinus = isMinus(previous, builder);
                    //如果是负号
                    if (isMinus) {
                        builder.append(c);
                        continue;
                    }
                }


                //取出当前运算符号
                String cs = "" + c;
                //运算符号节点
                Node symbol = buildNode(cs, null, null);
                //数值节点
                Node number = buildNode(builder.toString(), symbol, null);
                if (previous != null) {
                    //如果上一个节点不为空，则设置数值的上一个节点为number
                    previous.setNext(number);
                    number.setPrevious(previous);
                }
                //设置运算符号的上一个节点为数值
                symbol.setPrevious(number);
                //设置上一个节点为运算符号
                previous = symbol;

                if (first == null) {
                    //如果第一个节点为空，记录下第一个节点的
                    first = number;
                    number.setPrevious(null);
                }
                //重置builder
                builder = new StringBuilder(8);
            } else {
                //判断是否是占位符
                if (c == LEFT_CURLY_BRACES) {
                    start = true;
                    continue;
                } else if (c == RIGHT_CURLY_BRACES) {
                    start = false;
                    String s = listener.parsingFormulaProperties(property.toString());
                    builder.append(s);
                    //重置property属性
                    property = new StringBuilder(8);
                }
                if (start) {
                    property.append(c);
                } else {
                    //该字符为数值的一部分，直接添加进去
                    if (c != RIGHT_CURLY_BRACES) {
                        builder.append(c);
                    }
                }
                //如果是最后一个，则添加数值
                if (i == chars.length - 1) {
                    Node number = buildNode(builder.toString(), null, previous);
                    if (previous == null) {
                        first = number;
                    } else {
                        previous.setNext(number);
                    }
                }
            }

        }
        //检查公式是否正确
        if (checkFormula(ride, add)) {
            return String.valueOf(first.getValue());
        }
        return operation(first, ride, add, scale);
    }

    /**
     * 判断当前符号"-"是代表减号还是负号
     * @param previous 上一个节点
     * @param builder 当前累计的字符
     * @return 是否负号
     */
    private static boolean isMinus(Node previous, StringBuilder builder) {
        //如果是减号，需要判断是否是负号
        //判断前一个字符是不是运算符号或者为空
        boolean isMinus = previous == null && StringUtils.isBlank(builder.toString());
        //上一个节点为空，并且当前为减号，则该减号为负号
        if (previous != null && StringUtils.isBlank(builder.toString())) {
            String s = previous.getValue().toString();
            //上一个节点是运算符号，当前也是减号，则这个减号为负号
            //当前为减号，上一个节点不为空，且上一个节点也不是运算符号，则该减号为减号
            isMinus = checkOperation(s);
        }
        return isMinus;
    }


    private static boolean checkOperation(String symbol) {
        return ADD_STR.equals(symbol) || MUL_STR.equals(symbol) || DIV_STR.equals(symbol) || SUB_STR.equals(symbol);
    }

    private static boolean checkFormula(boolean ride, boolean add) {
        return !ride && !add;
    }

    /**
     * 快速创建node对象
     * @param value 值
     * @param next 下一个node
     * @param previous 上一个node
     * @return 返回node对象
     */
    private static Node buildNode(Object value, Node next, Node previous) {
        Node node = new Node();
        node.setValue(value);
        node.setNext(next);
        node.setPrevious(previous);
        return node;
    }


    /**
     * 搜索指定节点的开始节点
     * @param node 指定的节点
     * @return 节点的最开始节点
     */
    private static Node getFirstNode(Node node) {
        for (;;) {
            if (node.getPrevious() != null) {
                node = node.getPrevious();
            } else {
                break;
            }
        }
        return node;
    }

    /**
     * 获取指定node链路的所有数据合并
     * @param node node节点
     * @return node链路合并的值
     */
    private static StringBuilder getNodeLinkValue(Node node) {
        StringBuilder builder = new StringBuilder();
        for(;;) {
            builder.append(node.getValue());
            if (node.getNext() != null) {
                node = node.getNext();
            } else {
                break;
            }
        }
        return builder;
    }

    /**
     *  根据解析公式结果，进行加、减、乘、除操作
     * @param node 解析后的公式数据
     * @param ride 是否有乘、除rwkt
     * @param add 是否有加、减操作
     * @param scale 保留小数位数（当有除不尽的数据时，保留此小数点数据，其它的四舍五入）
     * @return 公式计算结果
     */
    private static String operation(Node node, boolean ride, boolean add, int scale) {
        Node current = null;
        if (ride) {
            current = doAlgorithm(node, MUL_STR, DIV_STR, scale);
        }
        if (add) {
            current = doAlgorithm(node, ADD_STR, SUB_STR, scale);
        }
        return (String) current.getValue();
    }

    /**
     * 执行加、减、乘、除计算
     * @param node 解析后的公式节点数据
     * @param symbolOne 运算符号一
     * @param symbolTwo 运算符号二
     * @param scale 保留小数位数（当有除不尽的数据时，保留此小数点数据，其它的四舍五入）
     * @return 执行计算结果
     */
    private static Node doAlgorithm(Node node, String symbolOne, String  symbolTwo, int scale) {
        Node current = node;
        for (;;) {
            String s = current.getValue().toString();
            //剩除优先计算
            String v;
            if (symbolOne.equals(s) || symbolTwo.equals(s)) {
                //取出运算符号的上一个值
                Node previous = current.getPrevious();
                String preValue = previous.getValue().toString();
                //取出运算符号的下一个值
                Node next = current.getNext();
                String nexValue = next.getValue().toString();

                //两个值相剩
                switch (s) {
                    case MUL_STR:
                        v = mul(preValue, nexValue);
                        break;
                    case DIV_STR:
                        v = div(preValue, nexValue, scale);
                        break;
                    case ADD_STR:
                        v = add(preValue, nexValue);
                        break;
                    case SUB_STR:
                        v = sub(preValue, nexValue);
                        break;
                    default:
                        throw new IllegalArgumentException("非法运算符号，本程序只支持【+,-,*,/】");
                }

                previous.setValue(v);
                previous.setNext(next.getNext());

                if (next.getNext() == null) {
                    current = previous;
                    break;
                } else {
                    current = next.getNext();
                    next.getNext().setPrevious(previous);
                }
                continue;
            }
            //该值为非乘法和除法，则判断是否已经到最后了，最后跳出
            if (current.getNext() == null) {
                break;
            }
            //取出下一个节点，则为当前节点
            current = current.getNext();
        }
        return current;
    }



}
