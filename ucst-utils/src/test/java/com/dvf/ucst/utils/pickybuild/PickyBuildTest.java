package com.dvf.ucst.utils.pickybuild;

import com.dvf.ucst.utils.pickybuild.PickyBuildTest.NoFwdBakStrBuildTest.NoFwdBakStrBuild.NoFwdBakStrElement;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PickyBuildTest {



    /**
     * Tests of a simple implementation of [PickyBuild] that will be used to test
     * the algorithms in [PickyBuildGenerator]. Gotta make sure the test is valid :)
     */
    static final class NoFwdBakStrBuildTest {

        @Test
        void assertConflictsNoModify() {
            final NoFwdBakStrBuild build = NoFwdBakStrBuild.of(
                    "You could not live with your own failure.",
                    "And where did that bring you?",
                    "Back to me."
            );
            // test implementation-specific behaviour for adding things that will fail:
            for (final NoFwdBakStrElement element : build.getAllContents()) {
                assertFalse(build.addIfNoConflicts(new NoFwdBakStrElement(element.getElementContent())));
                assertFalse(build.addIfNoConflicts(new NoFwdBakStrElement(element.getReversedElementContent())));
            }
            // test that adding things that are already contained succeeds:
            final int size = build.getAllContents().size();
            for (final NoFwdBakStrElement element : build.getAllContents()) {
                assertTrue(build.addIfNoConflicts(element));
            }
            // and make sure adding contained items causes no (externally visible) changes:
            assertEquals(size, build.getAllContents().size());
        }



        /**
         * In this implementation, items will conflict if their string values are the
         * same either in the forward or backward direction.
         */
        static final class NoFwdBakStrBuild implements PickyBuild<NoFwdBakStrElement> {

            private final Set<NoFwdBakStrElement> contents;

            NoFwdBakStrBuild(final Set<NoFwdBakStrElement> contents) {
                this.contents = new HashSet<>();
                for (final NoFwdBakStrElement element : contents) {
                    assert addIfNoConflicts(element);
                }
            }

            @Override
            public PickyBuild<NoFwdBakStrElement> copy() {
                return new NoFwdBakStrBuild(getAllContents());
            }

            @Override
            public boolean addIfNoConflicts(final NoFwdBakStrElement item) {
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
            public Set<NoFwdBakStrElement> getAllContents() {
                return Collections.unmodifiableSet(contents);
            }

            // static convenience producer/constructor method.
            static NoFwdBakStrBuild of(String... elementsContents) {
                return new NoFwdBakStrBuild(
                        NoFwdBakStrElement.setOf(elementsContents)
                );
            }

            static final class NoFwdBakStrElement implements PickyBuildElement<NoFwdBakStrElement> {

                private final String elementContent;
                private final Set<Set<NoFwdBakStrElement>> friends;

                NoFwdBakStrElement(final String elementContent) {
                    this.elementContent = elementContent;
                    friends = Collections.emptySet();
                }
                NoFwdBakStrElement(final String elementContent, final Set<Set<NoFwdBakStrElement>> friends) {
                    this.elementContent = elementContent;
                    this.friends = friends;
                }

                @Override
                public Set<Set<NoFwdBakStrElement>> getPickyBuildFriends() {
                    return Collections.emptySet();
                }
                String getElementContent() {
                    return elementContent;
                }
                private String getReversedElementContent() {
                    return new StringBuilder(getElementContent()).reverse().toString();
                }

                static Set<NoFwdBakStrElement> setOf(final String... contents) {
                    return Arrays.stream(contents)
                            .map(NoFwdBakStrElement::new)
                            .collect(Collectors.toSet());
                }
            }
        }
    }

}