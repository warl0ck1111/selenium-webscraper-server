package com.example.seleniumdemo.others;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.WordUtils;
import org.intellij.lang.annotations.Language;
import org.jsoup.parser.Parser;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.*;

@UtilityClass
@Slf4j
public class SharedSemanticUtils {

    private final int permutationsMaxLength = 4;

    private final ObjectMapper csvMapper = new CsvMapper();
    //create a text object factory
    private final TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
    //load all languages:
    private List<LanguageProfile> languageProfiles = null;

    static {
        try {
            languageProfiles = new LanguageProfileReader().readAllBuiltIn();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //build language detector:
    private final LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
            .withProfiles(languageProfiles)
            .build();
    private final List<String> letters =
            Arrays.asList(
                    "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
            );
    private final List<String> pornForbiddenSemantics =
            Arrays.asList(
                    "porn", "porno", "sex film", "sex movie", "sex webcam", "sex web cam", "sex tape", "sextape",
                    "adult film", "adult movie", "adult webcam", "adult web cam", "fuck film", "fuck movie",
                    "fuck webcam", "fuck web cam", "pornstar", "xxx", "sexe film", "sexe webcam", "sexe web cam", "adulte film", "sexe tape", "sexetape",
                    "salope", "bitch", "fucked", "baisee", "sodomy", "sodomie", "sodomite", "double penetration", "fistfuck", "fistfucking", "fuck", "fucking", "doublepenetration",
                    "pornhub", "xvideos", "youporn", "xhamster", "sex video", "nu", "naked", "nue"
            );

    private final List<String> googleMutiWordsCharacters = Arrays.asList("'", "’", "-");


    public List<String> findAdditionalElements(List<String> newElements, List<String> existingElements) {

        //no new elements
        if (newElements == null || newElements.size() == 0) {
            return new ArrayList<>();//NONE are new elements
        }

        //no existing elements
        if (existingElements == null || existingElements.size() == 0) {
            return newElements;//ALL are new elements and they are not null
        }

        boolean found;
        List<String> additionalElements = new ArrayList<>();
        for (String newElement : newElements) {
            found = false;
            for (String existingElement : existingElements) {
                if (newElement.equalsIgnoreCase(existingElement)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                additionalElements.add(newElement);
            }
        }

        return additionalElements;
    }

    public List<String> toLowerCaseAndSortAlphabetically(Set<String> elements) {

        //no new elements
        if (elements == null || elements.size() == 0) {
            return new ArrayList<>();//NONE are new elements
        } else {
            Set<String> resultSet = new HashSet<>();
            for (String element : elements) {
                resultSet.add(replaceDuplicateSpacesBySingleSpace(StringUtils.lowerCase(element)));
            }
            List<String> result = new ArrayList<>(resultSet);
            Collections.sort(result);
            return result;
        }
    }

    public List<String> sortAlphabetically(Set<String> elements) {
        if (elements == null || elements.size() == 0) {
            return new ArrayList<>();
        } else {
            List<String> result = new ArrayList<>(elements);
            Collections.sort(result);
            return result;
        }
    }

    public boolean isListChanged(List<String> newList, List<String> oldList) {

        if (newList == null && oldList == null) {
            return false;
        }

        if (newList == null) {
            return true;
        }

        if (oldList == null) {
            return true;
        }

        if (oldList.size() != newList.size()) {
            return true;
        } else {
            boolean isFound;
            for (String newEntry : newList) {
                isFound = false;
                for (String oldEntry : oldList) {
                    if (oldEntry.equalsIgnoreCase(newEntry)) {
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    return true;
                }
            }
            return false;
        }

    }

    public List<String> getPornForbiddenSemantics() {
        return pornForbiddenSemantics;
    }

    public List<String> decode(List<String> texts) {
        List<String> result = new ArrayList<>();
        if (texts != null) {
            for (String text : texts) {
                if (StringUtils.isNotBlank(text)) {
                    result.add(decode(text));
                }
            }
        }
        return result;
    }

    public String decode(String text) {
        try {
            if (StringUtils.isNotBlank(text)) {
                return unescapeJava(fixEncodingIssues(Parser.unescapeEntities(HtmlUtils.htmlUnescape(URLDecoder.decode(text, StandardCharsets.UTF_8)), true)));
            } else {
                return text;
            }
        } catch (Exception e1) {
            //log.info("******decode/decode error : " + text);
            try {
                return unescapeJava(fixEncodingIssues(Parser.unescapeEntities(HtmlUtils.htmlUnescape(text), true)));
            } catch (Exception e2) {
                try {
                    return unescapeJava(fixEncodingIssues(Parser.unescapeEntities(text, true)));
                } catch (Exception e3) {
                    return unescapeJava(fixEncodingIssues(text));
                }
            }
        }
    }

    private String unescapeJava(String word) {
        try {
            return StringEscapeUtils.unescapeJava(word);
        } catch (Exception e) {
            //log.info("******decode/decode error : " + label);
            return word;
        }
    }


    public String fixEncodingIssues(String word) {
        if (StringUtils.isNotBlank(word)) {
            return word.replace("Ã¢", "â")
                    .replace("Ã©", "é")
                    .replace("Ã¨", "è")
                    .replace("Ãª", "ê")
                    .replace("Ã«", "ë")
                    .replace("Ã®", "î")
                    .replace("Ã¯", "ï")
                    .replace("Ã´", "ô")
                    .replace("Ã¶", "ö")
                    .replace("Ã¹", "ù")
                    .replace("Ã¼", "ü")
                    .replace("Ã§", "ç")
                    .replace("Ã", "à")
                    .replace("&#39;", "'")
                    .replace("&nbsp;", " ")
                    .replace("&quot;", "\"")
                    .replace("&lt;", "<")
                    .replace("&gt;", ">")
                    .replace("&gt;", ">")
                    .replace("&iexcl;", "¡")
                    .replace("&cent;", "¢")
                    .replace("&pound;", "£")
                    .replace("&curren;", "¤")
                    .replace("&yen;", "¥")
                    .replace("&brvbar;", "¦")
                    .replace("&sect;", "§")
                    .replace("&uml;", "¨")
                    .replace("&copy;", "©")
                    .replace("&ordf;", "ª")
                    .replace("&laquo;", "«")
                    .replace("&not;", "¬")
                    .replace("&shy;", "")
                    .replace("&reg;", "®")
                    .replace("&macr;", "¯")
                    .replace("&deg;", "°")
                    .replace("&plusmn;", "±")
                    .replace("&sup2;", "²")
                    .replace("&sup3;", "³")
                    .replace("&acute;", "´")
                    .replace("&micro;", "µ")
                    .replace("&para;", "¶")
                    .replace("&middot;", "·")
                    .replace("&cedil;", "¸")
                    .replace("&sup1;", "¹")
                    .replace("&ordm;", "º")
                    .replace("&raquo;", "»")
                    .replace("&frac14;", "¼")
                    .replace("&frac12;", "½")
                    .replace("&frac34;", "¾")
                    .replace("&iquest;", "¿")
                    .replace("&Agrave;", "À")
                    .replace("&Aacute;", "Á")
                    .replace("&Acirc;", "Â")
                    .replace("&Atilde;", "Ã")
                    .replace("&Auml;", "Ä")
                    .replace("&Aring;", "Å")
                    .replace("&AElig;", "Æ")
                    .replace("&Ccedil;", "Ç")
                    .replace("&Egrave;", "È")
                    .replace("&Eacute;", "É")
                    .replace("&Ecirc;", "Ê")
                    .replace("&Euml;", "Ë")
                    .replace("&Igrave;", "Ì")
                    .replace("&Iacute;", "Í")
                    .replace("&Icirc;", "Î")
                    .replace("&Iuml;", "Ï")
                    .replace("&ETH;", "Ð")
                    .replace("&Ntilde;", "Ñ")
                    .replace("&Ograve;", "Ò")
                    .replace("&Oacute;", "Ó")
                    .replace("&Ocirc;", "Ô")
                    .replace("&Otilde;", "Õ")
                    .replace("&Ouml;", "Ö")
                    .replace("&times;", "×")
                    .replace("&Oslash;", "Ø")
                    .replace("&Ugrave;", "Ù")
                    .replace("&Uacute;", "Ú")
                    .replace("&Ucirc;", "Û")
                    .replace("&Uuml;", "Ü")
                    .replace("&Yacute;", "Ý")
                    .replace("&THORN;", "Þ")
                    .replace("&szlig;", "ß")
                    .replace("&agrave;", "à")
                    .replace("&aacute;", "á")
                    .replace("&acirc;", "â")
                    .replace("&atilde;", "ã")
                    .replace("&auml;", "ä")
                    .replace("&aring;", "å")
                    .replace("&aelig;", "æ")
                    .replace("&ccedil;", "ç")
                    .replace("&egrave;", "è")
                    .replace("&eacute;", "é")
                    .replace("&ecirc;", "ê")
                    .replace("&euml;", "ë")
                    .replace("&igrave;", "ì")
                    .replace("&iacute;", "í")
                    .replace("&icirc;", "î")
                    .replace("&iuml;", "ï")
                    .replace("&eth;", "ð")
                    .replace("&ntilde;", "ñ")
                    .replace("&ograve;", "ò")
                    .replace("&oacute;", "ó")
                    .replace("&ocirc;", "ô")
                    .replace("&otilde;", "õ")
                    .replace("&ouml;", "ö")
                    .replace("&divide;", "÷")
                    .replace("&oslash;", "ø")
                    .replace("&ugrave;", "ù")
                    .replace("&uacute;", "ú")
                    .replace("&ucirc;", "û")
                    .replace("&uuml;", "ü")
                    .replace("&yacute;", "ý")
                    .replace("&thorn;", "þ")
                    .replace("&yuml;", "ÿ")
                    .replace("&euro;", "€")
                    .replace("&amp;", "&")
                    .replace('\u0092', ' ')//space
                    .replace('\u0091', ' ')//space
                    .replace('\u0099', ' ')//space
                    .replace('\u0085', ' ')//space
                    .replace('\u0099', ' ')//space
                    .replace('\u0000', ' ')//null character
                    .replace('\u008C', ' ')//not sure what this is
                    .replace('\u0008', ' ');//backspace character
        } else {
            return word;
        }
    }

    private static boolean checkIfLabelIsASynonymUsingContainsAllWordsLogicIfLabelIsTooLongForPermutations(String streamLinedLabelUniqueWords, Set<String> existingLabelsWithPermutations) {
        //log.info("checkWithContainsAllWordsIfLabelIsTooLongForPermutations/streamLinedLabelUniqueWords=" + streamLinedLabelUniqueWords);
        //log.info("checkWithContainsAllWordsIfLabelIsTooLongForPermutations/existingLabelsWithPermutations=" + existingLabelsWithPermutations);
        if (StringUtils.isNotBlank(streamLinedLabelUniqueWords)) {
            String[] split = streamLinedLabelUniqueWords.split(" ");
            if (split.length > permutationsMaxLength) {
                for (String existingLabelsWithPermutation : existingLabelsWithPermutations) {
                    if (WordUtils.containsAllWords(existingLabelsWithPermutation, split)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public String removeAccentAndDoubleSpacesAndPunctuationAndDoLowerCase(String word) {
        if (word != null) {
            return StringUtils.trim(
                    StringUtils.lowerCase(
                            StringUtils.stripAccents(
                                    Normalizer.normalize(word, Normalizer.Form.NFC)
                                            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")//accent
                                            .replaceAll("\\p{P}", " ")//ponctuation
                                            .replace("%20", " ")//word errors
                                            .replace("œ", "oe")//save those ones
                                            .replace("æ", "ae")//save those ones
                                            .replace("®", "")//remove those ones
                                            .replace('\u0092', ' ')//space
                                            .replace('\u0091', ' ')//space
                                            .replace('\u0099', ' ')//space
                                            .replace('\u0085', ' ')//space
                                            .replace('\u0099', ' ')//space
                                            .replace('\u0000', ' ')//null character
                                            .replace('\u008C', ' ')//not sure what this is
                                            .replace('\u0008', ' ')//backspace character
                                            .replaceAll("\\s+", " ")//space
                            )));
        } else {
            return null;
        }
    }

    //CAREFULL BEFORE CALLING THIS : it generates MANY PERMUTATIONS
    //USUAL CONSTRAINTS
    //if (!containsStopWord(label, language) &&  // LABEL WITH STOP WORDS ARE NOT PERMUTATED
    //                label.split(" ").length <= 4) {//LENGTH IS NOT TOO LONG
    public List<String> generatePermutationStringsExceptTheStringItself_CAREFULL_BEFORE_CALLING_THIS(String word) {
        List<String> result = new ArrayList<>();
        if (StringUtils.isNotEmpty(word)) {
            StringBuilder permutation;
            word = replaceDuplicateSpacesBySingleSpace(word);
            List<List<String>> permutations = generatePermutations(new ArrayList<>(Arrays.asList(word.split(" "))));
            for (List<String> wordElements : permutations) {
                permutation = new StringBuilder();
                for (String wordElement : wordElements) {
                    permutation.append(wordElement).append(" ");
                }
                if (StringUtils.isNotEmpty(permutation.toString())) {
                    result.add(StringUtils.trim(permutation.toString()));
                }
            }
        }
        if (result.size() > 0) {
            return result.subList(1, result.size());//except the keyword label
        }
        return result;
    }

    private List<List<String>> generatePermutations(List<String> original) {
        if (original.size() == 0) {
            List<List<String>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        }
        String firstElement = original.remove(0);
        List<List<String>> returnValue = new ArrayList<>();
        List<List<String>> permutations = generatePermutations(original);
        for (List<String> smallerPermutated : permutations) {
            for (int index = 0; index <= smallerPermutated.size(); index++) {
                List<String> temp = new ArrayList<>(smallerPermutated);
                temp.add(index, firstElement);
                returnValue.add(temp);
            }
        }
        return returnValue;
    }

    public String removePlural(String word) {
        if (word != null) {
            StringBuilder buffer = new StringBuilder();
            String[] strings = word.split(" ");
            for (String string : strings) {
                buffer.append(string.replaceAll("s$", "").replaceAll("x$", "")).append(" ");
            }
            //need to trim to deal with keyword nbeing "s p 500" since this will remove the initial "s"
            return StringUtils.trim(buffer.toString());
        } else {
            return null;
        }
    }


    public String removeSpaces(String string) {
        if (string != null) {
            return StringUtils.trim(
                    string
                            .replace("%20", " ")//word errors
                            .replace('\u0092', ' ')//space
                            .replace('\u0091', ' ')//space
                            .replace('\u0099', ' ')//space
                            .replace('\u0085', ' ')//space
                            .replace('\u0099', ' ')//space
                            .replace('\u0000', ' ')//null character
                            .replace('\u008C', ' ')//not sure what this is
                            .replace('\u0008', ' ')//backspace character
                            .replaceAll("\\s+", ""));
        } else {
            return null;
        }
    }

    public String replaceDuplicateSpacesBySingleSpace(String string) {
        if (string != null) {
            return StringUtils.trim(
                    string
                            .replace('\u0092', ' ')//space
                            .replace('\u0091', ' ')//space
                            .replace('\u0099', ' ')//space
                            .replace('\u0085', ' ')//space
                            .replace('\u0099', ' ')//space
                            .replace('\u0000', ' ')//null character
                            .replace('\u008C', ' ')//not sure what this is
                            .replace('\u0008', ' ')//backspace character
                            .replaceAll("\\s+", " "));
        } else {
            return null;
        }
    }

    public String getCSVString(List<String> strings) {
        StringBuilder result = new StringBuilder();
        if (strings != null && strings.size() > 0) {
            String cleanedCandidate;
            for (String candidate : strings) {
                cleanedCandidate = replaceDuplicateSpacesBySingleSpace(candidate);//remove duplicate space
                if (StringUtils.isNotBlank(cleanedCandidate)) {
                    result.append(cleanedCandidate).append(",");
                }
            }
            if (result.toString().endsWith(",")) {
                return result.substring(0, result.length() - 1);
            }
        }
        return result.toString();
    }

    private List<List<String>> splitList(List<String> labels, int partitionSize) {
        log.info("splitList/inputList.size=" + labels.size());
        List<List<String>> partitions = new ArrayList<>();
        for (int i = 0; i < labels.size(); i += partitionSize) {
            partitions.add(labels.subList(i, Math.min(i + partitionSize, labels.size())));
        }
        log.info("splitList/numberOfBatches=" + partitions.size());
        return partitions;
    }

    public static Integer computeNbOfWordForText(String content) {
        if (StringUtils.isNotBlank(content)) {
            content = SharedSemanticUtils.replaceDuplicateSpacesBySingleSpace(content);
            return content.split(" ").length;
        } else {
            return 0;
        }
    }

    public static int getNbOfWords(String label) {
        int total = 0;
        if (label != null) {
            total = label.split(" ").length;
        }
        return total;
    }

    public static int getNbOfWordsFromList(List<String> labels) {
        int totalWords = 0;
        if (labels != null) {
            for (String label : labels) {
                totalWords += getNbOfWords(label);
            }
        }
        return totalWords;
    }

}
