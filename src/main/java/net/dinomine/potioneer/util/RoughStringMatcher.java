package net.dinomine.potioneer.util;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public final class RoughStringMatcher {

    private static final Pattern WORD_PATTERN =
            Pattern.compile("[a-z0-9]+");

    public static boolean roughlyMatches(
            String base,
            String message,
            double minWordMatchPercent,
            double minCharSimilarity,
            int minSharedWords
    ) {
        if (base == null || message == null) return false;

        List<String> baseWords = tokenize(base);
        List<String> msgWords = tokenize(message);

        if (baseWords.isEmpty() || msgWords.isEmpty()) return false;

        // Word overlap
        Set<String> baseSet = new HashSet<>(baseWords);
        Set<String> msgSet = new HashSet<>(msgWords);

        int sharedWords = 0;
        for (String w : msgSet) {
            if (baseSet.contains(w)) {
                sharedWords++;
            }
        }

        double wordMatchPercent =
                (double) sharedWords / msgSet.size();

        if (sharedWords < minSharedWords) return false;
        if (wordMatchPercent < minWordMatchPercent) return false;

        // Character similarity (Jaccard over characters)
        double charSimilarity =
                characterSimilarity(base, message);

        return charSimilarity >= minCharSimilarity;
    }

    private static List<String> tokenize(String s) {
        s = s.toLowerCase(Locale.ROOT);
        Matcher m = WORD_PATTERN.matcher(s);

        List<String> words = new ArrayList<>();
        while (m.find()) {
            words.add(m.group());
        }
        return words;
    }

    private static double characterSimilarity(String a, String b) {
        Set<Character> setA = toCharSet(a);
        Set<Character> setB = toCharSet(b);

        Set<Character> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);

        Set<Character> union = new HashSet<>(setA);
        union.addAll(setB);

        return union.isEmpty()
                ? 0.0
                : (double) intersection.size() / union.size();
    }

    private static Set<Character> toCharSet(String s) {
        Set<Character> set = new HashSet<>();
        for (char c : s.toLowerCase(Locale.ROOT).toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                set.add(c);
            }
        }
        return set;
    }
}
