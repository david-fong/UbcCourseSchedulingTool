package com.dvf.ucst.utils.pickybuild;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PickyBuildGeneratorTest {

    @Test
    void test() {
        // TODO:
    }



    // (in this implementation, items with the same string also conflict. not just palindromes)
    static final class NoPalindromeBuild implements PickyBuild<NoPalindromeBuild.NoPalindromeElement> {

        private final Set<NoPalindromeElement> contents;

        NoPalindromeBuild(final Set<NoPalindromeElement> contents) {
            this.contents = new HashSet<>(contents);
        }

        @Override
        public PickyBuild<NoPalindromeElement> copy() {
            return new NoPalindromeBuild(getAllContents());
        }

        @Override
        public boolean addIfNoConflicts(final NoPalindromeElement item) {
            if (contents.contains(item)) {
                return true;

            } else {
                final String itemBackwards = item.getReversedElementContent();
                if (getAllContents().stream()
                        .anyMatch(element -> element.getElementContent().equals(item.getElementContent())
                                || element.elementContent.equals(itemBackwards))
                ) {
                    return false;

                } else {
                    contents.add(item);
                    return true;
                }
            }
        }

        @Override
        public Set<NoPalindromeElement> getAllContents() {
            return Collections.unmodifiableSet(contents);
        }

        // static convenience producer/constructor method.
        static NoPalindromeBuild of(String... elementsContents) {
            return new NoPalindromeBuild(Arrays.stream(elementsContents)
                    .map(NoPalindromeElement::new)
                    .collect(Collectors.toSet())
            );
        }

        static final class NoPalindromeElement implements PickyBuildElement<NoPalindromeElement> {
            private final String elementContent;
            NoPalindromeElement(final String elementContent) {
                this.elementContent = elementContent;
            }
            @Override
            public Set<Set<NoPalindromeElement>> getPickyBuildFriends() {
                return Collections.emptySet();
            }
            String getElementContent() {
                return elementContent;
            }
            private String getReversedElementContent() {
                return new StringBuilder(getElementContent()).reverse().toString();
            }
        }
    }

}