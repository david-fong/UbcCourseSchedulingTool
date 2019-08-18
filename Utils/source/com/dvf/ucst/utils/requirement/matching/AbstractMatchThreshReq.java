package com.dvf.ucst.utils.requirement.matching;

import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import com.dvf.ucst.utils.xml.XmlUtils;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Threshold is an inclusive lower bound greater than zero. Ie. if a test subject's
 * matches map to a sum greater than or equal to [threshold], then it is considered
 * to pass [this][Requirement].
 *
 * IMPORTANT: Matching is done through the [equals] method.
 * @param <T>
 */
public abstract class AbstractMatchThreshReq<T> implements MatchingRequirementIf<T> {

    final int threshold;
    private final Set<T> candidates;

    public final int getThreshold() {
        return threshold;
    }

    AbstractMatchThreshReq(int threshold, Set<T> candidates) {
        assert threshold > 0 : "threshold must be greater than zero";
        this.threshold  = threshold;
        this.candidates = Collections.unmodifiableSet(candidates);
    }

    public AbstractMatchThreshReq(final Element mtrElement, final Function<Element, T> candidateParser) throws MalformedXmlDataException {
        this.threshold = Integer.parseInt(XmlUtils.getMandatoryAttr(
                mtrElement, Xml.COUNT_MTR_THRESHOLD_ATTR
        ).getValue());
        this.candidates = Collections.unmodifiableSet(
                XmlUtils.getChildElementsByTagName(
                        mtrElement, Xml.CANDIDATE_TAG
                ).stream().map(candidateParser).collect(Collectors.toSet())
        );
    }

    final Set<T> getCandidates() {
        return candidates;
    }



    public enum Xml implements XmlUtils.XmlConstant {
        COUNT_MTR_THRESHOLD_ATTR ("threshold"),

        CANDIDATE_TAG ("Candidate"),
        CANDIDATE_NAME_ATTR ("name"),
        ;
        private final String value;

        Xml(String value) {
            this.value = value;
        }

        @Override
        public String getXmlConstantValue() {
            return value;
        }
    }

}
