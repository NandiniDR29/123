package com.exigen.ren.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

public class CommonMethods {
    protected static final Logger LOGGER = LoggerFactory.getLogger(CommonMethods.class);

    /** Get random element from list.
     * @param elements List of elements
     * @param <T> Type of elements
     * @return Random element from list with type <T>
     */
    public static <T> T getRandomElementFromList(List<T> elements) {
        if (elements.isEmpty()) {
            throw new IllegalArgumentException("List is empty");
        }
        T randomElement = elements.get(new SecureRandom().nextInt(elements.size()));
        LOGGER.info(String.format("Element '%s' is selected", randomElement.toString()));
        return randomElement;
    }

    /**
     * @param elements  List of elements
     * @param elementsToExclude  List of elements to exclude
     * @param <T> Type of elements
     * @return Random element from list with type <T>
     */
    public static <T> T getRandomElementFromListWithExclude(List<T> elements, List<T> elementsToExclude) {
        List<T> aDiffB = elements.stream()
                .filter(i -> !elementsToExclude.contains(i))
                .collect(Collectors.toList());
        return getRandomElementFromList(aDiffB);
    }
}

