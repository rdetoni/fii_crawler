package com.fii.crawler.service;

import com.fii.crawler.entities.BrazilianStock;
import com.fii.crawler.entities.FII;
import com.gargoylesoftware.htmlunit.Cache;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CrawlerService {
    private static final Cache cache = new Cache();

    @Value("${infomoney.url.ticker}")
    private String ticker;

    public FII getDataFromFii(String code) throws IOException {

        String url = "https://www.fundsexplorer.com.br/funds/" + code.toUpperCase();
        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.setCache(cache);
        HtmlPage page = webClient.getPage(url);

        //gets price
        HtmlParagraph paragraphElement = (HtmlParagraph) page.getByXPath("//section//div//div//div//p").get(1);
        String price = paragraphElement.getFirstChild().getNodeValue();

        //gets last dividend and p/vp
        List<HtmlDivision> htmlDivisionList = page.getByXPath("//section//div//div");
        String lastDividend = null;
        String pVp = null;
        String equityValue = null;
        for(HtmlDivision div : htmlDivisionList) {
            try{
                String valueLabel = div.getNextElementSibling().getFirstChild().getNextSibling().getFirstChild().getNodeValue();
                String value = div.getNextElementSibling().getFirstChild().getNextSibling().getFirstChild().getNodeValue();
                if(valueLabel.equalsIgnoreCase("Último Rendimento")){
                    lastDividend = div.getNextElementSibling().getFirstChild().getNextSibling().getNextSibling()
                            .getNextSibling().getFirstChild().getNextSibling().getNextSibling().getNextSibling()
                            .getFirstChild().getNodeValue().trim().replaceAll(",", ".");
                }

                if(value.contains("VP")){
                    pVp = div.getNextElementSibling().getFirstChild().getNextSibling().getNextSibling()
                            .getNextSibling().getFirstChild().getNextSibling().getFirstChild().getNodeValue().trim()
                            .replaceAll(",", ".");
                }

                if(value.equalsIgnoreCase("Valor Patrimonial")){
                    equityValue = div.getNextElementSibling().getFirstChild().getNextSibling().getNextSibling()
                            .getNextSibling().getFirstChild().getNextSibling().getFirstChild().getNextSibling()
                            .getNextSibling().getNodeValue().trim().replaceAll(",", ".");
                }

                if(lastDividend != null && pVp != null && equityValue != null){
                    break;
                }
            }catch(NullPointerException e){
                System.out.println("Null pointer executing operations. Continuing loop.");
            }
        }

        return new FII(code, price, lastDividend, pVp, equityValue);
    }

    public BrazilianStock getBrazilianStockByName(String name) throws IOException{
        String url = "https://www.infomoney.com.br/cotacoes/b3/acao/" + name;
        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.setCache(cache);
        HtmlPage page = webClient.getPage(url);

        return new BrazilianStock(name, extractString(ticker, page.getBaseURL().getPath(), 1).toUpperCase());
    }

    private String extractString(String regex, String text, int groupIndex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(groupIndex) : "";
    }
}
