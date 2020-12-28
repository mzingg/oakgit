package oakgit.util;

import oakgit.engine.model.ContainerEntry;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;

public class Matchers {

    public static Matcher<ContainerEntry<?>> entryWithId(String id) {
        return new TypeSafeMatcher<>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("has desired id");
            }

            @Override
            protected boolean matchesSafely(ContainerEntry<?> containerEntry) {
                return containerEntry.getId().equals(id);
            }

            @Override
            protected void describeMismatchSafely(ContainerEntry<?> item, Description mismatchDescription) {
                if (item == null) {
                    mismatchDescription.appendText("is null");
                } else {
                    mismatchDescription.appendText("has wrong id ").appendValue(item.getId());
                }
            }
        };
    }

    public static Matcher<Optional<?>> isPresent() {
        return new TypeSafeMatcher<>() {
            @Override
            protected boolean matchesSafely(Optional<?> argument) {
                return argument.isPresent();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is present");
            }
        };
    }

    public static Matcher<Optional<?>> isEmpty() {
        return new TypeSafeMatcher<>() {
            @Override
            protected boolean matchesSafely(Optional<?> argument) {
                return argument.isEmpty();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is empty");
            }
        };
    }

    public static <T> Matcher<Optional<T>> isPresentAndIs(T operand) {
        return isPresentAnd(equalTo(operand));
    }

    public static <T> Matcher<Optional<T>> isPresentAnd(Matcher<? super T> matcher) {
        return new TypeSafeMatcher<>() {
            @Override
            protected boolean matchesSafely(Optional<T> argument) {
                return argument.isPresent() && matcher.matches(argument.get());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has value that is ");
                matcher.describeTo(description);
            }

            @Override
            protected void describeMismatchSafely(Optional<T> item, Description mismatchDescription) {
                item.ifPresentOrElse(
                        value -> {
                            mismatchDescription.appendText("value ");
                            matcher.describeMismatch(value, mismatchDescription);
                        },
                        () -> mismatchDescription.appendText("was empty")
                );
            }
        };
    }
}
