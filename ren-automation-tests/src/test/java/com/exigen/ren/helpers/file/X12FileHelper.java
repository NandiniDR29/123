package com.exigen.ren.helpers.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class X12FileHelper {

    public static List<String[]> getListOfCertificatesLinesFrom834File(File file, String certNumber) {

        String[] strArray = FileHelper.getFileContent(file).split("\n");
        List<String[]> certList = new ArrayList<>();
        int startIndexCertificateLoop = 0;

        while (!(startIndexCertificateLoop == -3)) {
            startIndexCertificateLoop = IntStream.range(startIndexCertificateLoop, strArray.length)
                    .filter(i -> String.format("REF*1L*%s~", certNumber).equals(strArray[i]))
                    .findFirst()
                    .orElse(-1) - 2;
            if (startIndexCertificateLoop == -3) {
                break;
            }
            int endIndexCertificateLoop = IntStream.range(startIndexCertificateLoop + 1, strArray.length)
                    .filter(i -> strArray[i].contains("REF*ZZ*CP"))
                    .findFirst()
                    .orElse(-1) + 1;

            certList.add(Arrays.copyOfRange(strArray, startIndexCertificateLoop, endIndexCertificateLoop));
            startIndexCertificateLoop = endIndexCertificateLoop;
        }
        return certList;
    }
}