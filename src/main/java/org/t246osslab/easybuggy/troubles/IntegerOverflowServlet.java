package org.t246osslab.easybuggy.troubles;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.t246osslab.easybuggy.core.utils.HTTPResponseCreator;
import org.t246osslab.easybuggy.core.utils.MessageUtils;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/iof" })
public class IntegerOverflowServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(IntegerOverflowServlet.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        BigDecimal thickness = null;
        BigDecimal thicknessM = null;
        BigDecimal thicknessKm = null;
        String strTimes = req.getParameter("times");
        int times = NumberUtils.toInt(strTimes, -1);
        try {
            Locale locale = req.getLocale();
            if (strTimes != null) {
                long multipleNumber = 1;
                if (times >= 0) {
                    for (int i = 0; i < times; i++) {
                        multipleNumber = multipleNumber * 2;
                    }
                    thickness = new BigDecimal(multipleNumber).divide(new BigDecimal(10)); // mm
                    thicknessM = thickness.divide(new BigDecimal(1000)); // m
                    thicknessKm = thicknessM.divide(new BigDecimal(1000)); // km
                }
            }

            StringBuilder bodyHtml = new StringBuilder();
            bodyHtml.append("<form action=\"iof\" method=\"post\">");
            bodyHtml.append(MessageUtils.getMsg("msg.question.reach.the.moon", locale));
            bodyHtml.append("<br><br>");
            if (times >= 0) {
                bodyHtml.append(
                        "<input type=\"text\" name=\"times\" size=\"2\" maxlength=\"2\" value=" + strTimes + ">");
            } else {
                bodyHtml.append("<input type=\"text\" name=\"times\" size=\"2\" maxlength=\"2\">");
            }
            bodyHtml.append("&nbsp; ");
            bodyHtml.append(MessageUtils.getMsg("label.times", locale) + " : ");
            if (times >= 0) {
                bodyHtml.append(thickness + " mm");
                if (thicknessM != null && thicknessKm != null) {
                    bodyHtml.append(thicknessM.intValue() >= 1 && thicknessKm.intValue() < 1 ? " = " + thicknessM + " m" : "");
                    bodyHtml.append(thicknessKm.intValue() >= 1 ? " = " + thicknessKm + " km" : "");
                }
                if (times == 42) {
                    bodyHtml.append(" : " + MessageUtils.getMsg("msg.answer.is.correct", locale));
                }
            }
            bodyHtml.append("<br><br>");
            bodyHtml.append("<input type=\"submit\" value=\"" + MessageUtils.getMsg("label.calculate", locale) + "\">");
            bodyHtml.append("<br><br>");
            bodyHtml.append(MessageUtils.getInfoMsg("msg.note.intoverflow", locale));
            bodyHtml.append("</form>");

            HTTPResponseCreator.createSimpleResponse(req, res, MessageUtils.getMsg("title.intoverflow.page", locale),
                    bodyHtml.toString());

        } catch (Exception e) {
            log.error("Exception occurs: ", e);
        }
    }
}
