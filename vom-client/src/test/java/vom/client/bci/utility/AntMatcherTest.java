package vom.client.bci.utility;

import org.junit.jupiter.api.Test;

import static vom.client.bci.utility.OpcodeUtils.antPathMatches;

class AntMatcherTest {

  @Test
  void shouldMatch() {
    assert antPathMatches("hi/hello/an0/ni-hao/java",
      "hi/hello/an0/ni-hao/java");

    assert antPathMatches("?est.java", "Test.java");
    assert !antPathMatches("?est.java", "est.java");
    assert antPathMatches("hi/hell?", "hi/hello");
    assert antPathMatches("hi?hello", "hi/hello");

    assert antPathMatches("*", "Test.java");
    assert antPathMatches("*", "hi");
    assert !antPathMatches("*", "hi/hello");
    assert !antPathMatches("*", "hi/Test.java");

    assert antPathMatches("**", "Test.java");
    assert antPathMatches("**", "hi");
    assert antPathMatches("**", "hi/hello");
    assert antPathMatches("**", "hi/Test.java");

    assert antPathMatches("**/*", "Test.java");
    assert antPathMatches("**/*", "hi");
    assert antPathMatches("**/*", "hi/hello");
    assert antPathMatches("**/*", "hi/Test.java");
    assert antPathMatches("**/*", "hi/hello/ni-hao/Test.java");
    assert antPathMatches("**/*/an?", "hi/an0");
    assert antPathMatches("**/*/an?", "hi/hello/an1");

    assert antPathMatches("**", "Test.java");
    assert antPathMatches("*.java", "Test.java");
    assert antPathMatches("**/*.java", "hi/Test.java");
    assert antPathMatches("**/*.java", "hi/hello/Test.java");

    assert antPathMatches("hi/hello/**/*", "hi/hello/Test.java");
    assert antPathMatches("hi/hello/**/*", "hi/hello/ni-hao");
    assert antPathMatches("hi/hello/**/*", "hi/hello/ni-hao/an0");

    assert !antPathMatches(
      "hi/**/ni-hao/*.java",
      "hi/hello/Test.java"
    );
    assert antPathMatches(
      "hi/**/ni-hao/*.java",
      "hi/hello/ni-hao/Test.java"
    );
    assert antPathMatches(
      "hi/**/ni-hao/*.java",
      "hi/hello/an0/ni-hao/Test.java"
    );
  }

}
