package eu.faircode.email;

import android.text.Html;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest(Html.class)
@RunWith(PowerMockRunner.class)
public class HtmlHelperTest {

    @Before
    public void setUp() {
        mockStatic(Html.class);
        when(Html.escapeHtml(any(CharSequence.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                return invocation.getArgument(0).toString()
                        .replace("&", "&amp;")
                        .replace("<", "&lt;");
            }
        });
    }

    @Test
    public void autolink() {

        testAutolink(
                "To visit http://www.example.org, go to http://www.example.org.",

                "" +
                        // The trailing comma must not be part of the URL.
                        "To visit <a href=\"http://www.example.org\">http://www.example.org</a>, " +
                        // The trailing dot must not be part of the URL.
                        "go to <a href=\"http://www.example.org\">http://www.example.org</a>.");

        testAutolink(
                "one hhhhh|spt://example.org three",

                // This string had been wrongly interpreted as a complete URL up to February 2019.
                "one hhhhh|spt://example.org three"
        );

        testAutolink(
                "https://example.org/search?q=%C3%A4&hl=nl",

                // The & should be encoded as &amp;, even though
                // most browsers can deal with this situation.
                "<a href=\"https://example.org/search?q=%C3%A4&amp;hl=nl\">" +
                        "https://example.org/search?q=%C3%A4&amp;hl=nl</a>"
        );

        testAutolink(
                "Go to \"http://example.org/\".",

                "Go to \"<a href=\"http://example.org/\">http://example.org/</a>\"."
        );

        testAutolink(
                "Go to <http://example.org/>.",

                // The < must be encoded as &lt;, to avoid confusion.
                "Go to &lt;<a href=\"http://example.org/\">http://example.org/</a>>."
        );

        testAutolink(
                "http://example.org/ and http://example.org/subdir/",

                // Each URL must be linked to its exact address, not just to a prefix.
                "" +
                        "<a href=\"http://example.org/\">http://example.org/</a> and " +
                        "<a href=\"http://example.org/subdir/\">http://example.org/subdir/</a>"
        );

        testAutolink(
                "http://example.org/ and http://example.org/ and http://example.org/",

                // When the same URL is mentioned multiple times,
                // each of the URLs must only appear a single time.
                "" +
                        "<a href=\"http://example.org/\">http://example.org/</a>" +
                        " and " +
                        "<a href=\"http://example.org/\">http://example.org/</a>" +
                        " and " +
                        "<a href=\"http://example.org/\">http://example.org/</a>"
        );
    }

    private void testAutolink(String input, String expectedOutput) {
        assertThat(HtmlHelper.autolink(input)).isEqualTo(expectedOutput);
    }
}
