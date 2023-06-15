package com.example.seleniumdemo.others;

import com.example.seleniumdemo.seleneum.model.BodyText;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.concurrent.Future;

/**
 * Created by ibantouchet on 29/03/2017.
 */
@Service
@Slf4j
public class JsoupSemanticKpisServiceImpl {


    public WordCountKpiVO getFutureSemanticKpis(String url) {

        log.info("getFutureSemanticKpis/url=" + url);

        //in any case this is the last try
        WordCountKpiVO wordCountKpiVO = new WordCountKpiVO(url);
        wordCountKpiVO.setIsWebSemanticKpisDataRetrieved(Boolean.TRUE);

        try {

            //connect to the website
            Connection connection = JsoupUtils.initConnection(url, 30);

            //get the HTML document
            Document doc = connection.get();

            if (connection.response() != null && doc != null && doc.body() != null && doc.body().text() != null) { //THERE IS SOME HTML BODY

                //log.info("getFutureSemanticKpis/success");
                wordCountKpiVO.setStatus("" + connection.response().statusCode());
                wordCountKpiVO.setFinalURL(connection.response().url().toString());
                updateWordCountKPI(wordCountKpiVO, doc);
                log.info("getFutureSemanticKpis/success for url=" + url);
                //log.info("getFutureSemanticKpis/payload.getBodytext().getP().size()=" + payload.getBodytext().getP().size());

            } else {
                log.info("getFutureSemanticKpis/error for url=" + url);
                //log.info("getFutureSemanticKpis/connection=" + connection);
                //log.info("getFutureSemanticKpis/doc=" + doc);
            }

        } catch (org.jsoup.HttpStatusException jsoupException) {

            log.info("getFutureSemanticKpis/FAIL jsoupException for url=" + url);
            log.info("getFutureSemanticKpis/FAIL jsoupException for jsoupException.getStatusCode()=" + jsoupException.getStatusCode());
            log.info("getFutureSemanticKpis/FAIL jsoupException for jsoupException=" + jsoupException);
            wordCountKpiVO.setStatus("" + jsoupException.getStatusCode());

        } catch (Exception e) {
            log.info("getFutureSemanticKpis/FAIL EXCEPTION for url=" + url);
            //e.printStackTrace();
        }
        return wordCountKpiVO;

    }

    private void updateWordCountKPI(WordCountKpiVO wordCountKpiVO, Document doc) {

        try {

            //TITLE
            if (StringUtils.isBlank(wordCountKpiVO.getMetaTitle())) {
                if (StringUtils.isNotBlank(doc.title())) {
                    wordCountKpiVO.setMetaTitle(SharedSemanticUtils.decode(doc.title()));
                    log.info("updateWordCountKPI/update title to " + wordCountKpiVO.getMetaTitle());
                }
            }

            //META DESC
            if (StringUtils.isBlank(wordCountKpiVO.getMetaDescription())) {
                Elements metaDescElement = doc.select("meta[name=description]");
                if (!metaDescElement.isEmpty() && StringUtils.isNotBlank(metaDescElement.attr("content"))) {
                    wordCountKpiVO.setMetaDescription(SharedSemanticUtils.decode(metaDescElement.attr("content")));
                    log.info("updateWordCountKPI/update description to " + wordCountKpiVO.getMetaDescription());
                }
            }

            //H1
            if (CollectionUtils.isEmpty(wordCountKpiVO.getH1())) {
                Elements h1Tags = doc.select("h1");
                if (!h1Tags.isEmpty() && StringUtils.isNotBlank(SharedSemanticUtils.getCSVString(h1Tags.eachText()))) {
                    for (Element h1 : h1Tags) {
                        wordCountKpiVO.getH1().add(SharedSemanticUtils.decode(h1.text()));
                    }
                    log.info("updateWordCountKPI/update H1 to " + wordCountKpiVO.getH1());
                }
            }

            //H2
            if (CollectionUtils.isEmpty(wordCountKpiVO.getH2())) {
                Elements h2Tags = doc.select("h2");
                if (!h2Tags.isEmpty() && StringUtils.isNotBlank(SharedSemanticUtils.getCSVString(h2Tags.eachText()))) {
                    for (Element h2 : h2Tags) {
                        wordCountKpiVO.getH2().add(SharedSemanticUtils.decode(h2.text()));
                    }
                    log.info("updateWordCountKPI/update H2 to " + wordCountKpiVO.getH2());
                }
            }

            //H3
            if (CollectionUtils.isEmpty(wordCountKpiVO.getH3())) {
                Elements h3Tags = doc.select("h3");
                if (!h3Tags.isEmpty() && StringUtils.isNotBlank(SharedSemanticUtils.getCSVString(h3Tags.eachText()))) {
                    for (Element h3 : h3Tags) {
                        wordCountKpiVO.getH3().add(SharedSemanticUtils.decode(h3.text()));
                    }
                    log.info("updateWordCountKPI/update H3 to " + wordCountKpiVO.getH3());
                }
            }

            //H4
            if (CollectionUtils.isEmpty(wordCountKpiVO.getH4())) {
                Elements h4Tags = doc.select("h4");
                if (!h4Tags.isEmpty() && StringUtils.isNotBlank(SharedSemanticUtils.getCSVString(h4Tags.eachText()))) {
                    for (Element h4 : h4Tags) {
                        wordCountKpiVO.getH4().add(SharedSemanticUtils.decode(h4.text()));
                    }
                    log.info("updateWordCountKPI/update H4 to " + wordCountKpiVO.getH4());
                }
            }

            //HTML
            if (StringUtils.isBlank(wordCountKpiVO.getHTML())) {
                wordCountKpiVO.setHTML(doc.outerHtml());
            }
            if (StringUtils.isBlank(wordCountKpiVO.getRawHTML())) {
                wordCountKpiVO.setRawHTML(doc.outerHtml());
            }
            //HN Hierarchy
//            wordCountKpiVO.setHtmlTitlesHierarchy(JsoupUtils.getHnHierarchyFromHtml(DomainUtils.getDomainNameNoException(wordCountKpiVO.getFinalURL()), wordCountKpiVO.getHTML(), customLogicService));

            //BODY CONTENT
            if (StringUtils.isBlank(wordCountKpiVO.getBodyContent()) && doc.body() != null) {
                String bodyContent = SharedSemanticUtils.decode(doc.body().text());
                //log.info("updateWordCountKPI="+bodyContent);
                wordCountKpiVO.setBodyContent(bodyContent);
                if (StringUtils.isNotBlank(bodyContent)) {
                    wordCountKpiVO.setWordsCount((long) bodyContent.split(" ").length);
                    //log.info("updateWordCountKPI/bodyContent words count=" + payload.getWordscount());
                }
            }

            //BODY EXTRACTS
            if (wordCountKpiVO.getBodyExtracts() == null || CollectionUtils.isEmpty(wordCountKpiVO.getBodyExtracts().getP())) {

                BodyText bodyExtracts = new BodyText();

                //PREPARE THE HTML TO KEEP ONLY WHAT WE ARE INTERESTED IN
                Safelist sl = Safelist.none();
                sl.addTags("body", "h1", "h2", "h3", "h4", "p", "div", "span");

                String cleanHtml = Jsoup.clean(doc.outerHtml(), sl);
                //log.info("updateWordCountKPI/cleanHtml after WHITE LIST=" + cleanHtml);

                //spaces
                cleanHtml = cleanHtml.replace("&nbsp;", " ");// spaces
                cleanHtml = cleanHtml.replaceAll("<\\s+", "<");// <   span
                cleanHtml = cleanHtml.replaceAll("\\s+>", ">");// span    >
                cleanHtml = cleanHtml.replace("<span", "<p");//to allow for consolidation span->p
                cleanHtml = cleanHtml.replace("span>", "p>");//to allow for consolidation span->p
                for (int i = 0; i < 6; i++) {
                    cleanHtml = cleanHtml.replaceAll("</p>\\s+<p>", " ");//consolidate
                    cleanHtml = cleanHtml.replaceAll("</div>\\s+<div>", " ");//consolidate
                }
                cleanHtml = SharedSemanticUtils.replaceDuplicateSpacesBySingleSpace(cleanHtml);//remove spaces
                //log.info("updateWordCountKPI/cleanHtml after CONSOLIDATION=" + cleanHtml);

                //NOW GO AND GET EXTRACTS
                Document docFromCleanHtml = Jsoup.parse(cleanHtml);

                //PARAGRAPHS
                Elements paragraphs = docFromCleanHtml.select("p");
                if (paragraphs != null) {
                    String paragraphContent;
                    for (Element p : paragraphs) {
                        paragraphContent = SharedSemanticUtils.decode(p.text());
                        if (paragraphContent != null && paragraphContent.length() > 250) {
                            //log.info("updateWordCountKPI/found paragraphContent="+paragraphContent.length());
                            //log.info("updateWordCountKPI/found paragraphContent="+paragraphContent);
                            bodyExtracts.getP().add(paragraphContent);
                        }
                    }
                }

                //DIVS
                Elements divs = docFromCleanHtml.select("div");
                if (divs != null) {
                    String divContent;
                    for (Element p : divs) {
                        if (p.children().isEmpty()) {
                            divContent = SharedSemanticUtils.decode(p.text());
                            if (divContent != null && divContent.length() > 250) {
                                //log.info("updateWordCountKPI/found divContent="+divContent.length());
                                //log.info("updateWordCountKPI/found divContent="+divContent);
                                bodyExtracts.getP().add(divContent);
                            }
                        }
                    }
                }

                wordCountKpiVO.setBodyExtracts(bodyExtracts);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
