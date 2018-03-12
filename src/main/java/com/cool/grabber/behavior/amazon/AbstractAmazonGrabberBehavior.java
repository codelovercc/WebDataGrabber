package com.cool.grabber.behavior.amazon;

import com.cool.grabber.behavior.AbstractGrabberBehavior;
import com.cool.grabber.exception.GrabberInvalidDataException;
import com.cool.util.Range;
import com.cool.util.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.util.StringUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by codelover on 18/3/9.
 */
public abstract class AbstractAmazonGrabberBehavior<T> extends AbstractGrabberBehavior<T> {

    private static final NumberFormat US_NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);

    /**
     * 使用美国地区的标准转换数字
     * @param source 数字的字符串
     * @return 转换好的数字
     * @throws GrabberInvalidDataException 无法转换为数字时抛出
     */
    protected Number parseNumber(String source) throws GrabberInvalidDataException {
        try {
            return US_NUMBER_FORMAT.parse(StringUtils.trimAllWhitespace(source));
        } catch (ParseException e) {
            throw new GrabberInvalidDataException("无法转换为数字", e);
        }
    }

    /**
     * 解析出星级
     * @param startsTitle 包含星级的字符串，例: 4.3 out of 5 stars
     * @return
     */
    protected float getStarts(String startsTitle) {
        startsTitle = startsTitle.split(" ")[0];
        return Float.parseFloat(StringUtils.trimAllWhitespace(startsTitle));
    }

    /**
     * 用于从排行页面抓取当前排行的商品类型
     * @param document 必须是销售排行的页面
     * @return 类型字符串 格式 父类*|当前类 例: Clothing,Shoes & Jewelry|Women|Clothing|Dresses
     * @throws GrabberInvalidDataException 没有找到类型数据时抛出
     */
    protected String findKindTree(Document document) throws GrabberInvalidDataException {
        Element element = document.getElementById("zg_browseRoot");
        if (element == null) {
            throw new GrabberInvalidDataException("获取类型列表失败");
        }
        String kind = "";
        Element browseUpElement = element;
        while ((browseUpElement = browseUpElement.select("li.zg_browseUp:eq(0)").first()) != null) {
            kind += browseUpElement.select("a").text().trim() + "|";
            browseUpElement = browseUpElement.nextElementSibling();
            if(browseUpElement == null){
                break;
            }
        }
        kind += element.select(".zg_selected").text().trim();
        return kind;
    }

    /**
     * 将字符串转换为金额范围，如果字符串中只包含一个金额19.9，那么范围将是19.9-19.9
     * @param strPrice like "$18.98 - $20.98" or "$13.99"
     * @return
     */
    protected Range<Double, Double> parsePrice(String strPrice) throws GrabberInvalidDataException {
        strPrice = StringUtils.trimAllWhitespace(strPrice);
        Range<Double, Double> priceRange = Range.getInstance();
        if (strPrice.contains("-")) {
            String[] prices = strPrice.replace("$", "").split("-");
            priceRange.setLeft(parseNumber(prices[0]).doubleValue());
            priceRange.setRight(parseNumber(prices[1]).doubleValue());
        } else {
            double p = parseNumber(strPrice.replace("$", "")).doubleValue();
            priceRange.setLeft(p);
            priceRange.setRight(p);
        }
        return priceRange;
    }
}
