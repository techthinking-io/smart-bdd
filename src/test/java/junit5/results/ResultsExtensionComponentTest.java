package junit5.results;

import junit5.results.model.TestCaseResult;
import junit5.results.model.TestSuiteResults;
import junit5.results.model.TestSuiteResultsId;
import junit5.results.model.TestSuiteResultsMetadata;
import junit5.results.undertest.AbortedTestCasesUnderTest;
import junit5.results.undertest.ExceptionThrownTestCasesUnderTest;
import junit5.results.undertest.ClassUnderTest;
import junit5.results.undertest.DisabledTestCasesUnderTest;
import junit5.results.undertest.FailedTestCasesUnderTest;
import junit5.utils.TestLauncher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static junit5.results.model.TestCaseResultBuilder.aTestCaseResult;
import static junit5.results.model.TestCaseStatus.FAILED;
import static junit5.results.model.TestCaseStatus.PASSED;
import static junit5.results.model.TestSuiteResultsId.testSuiteResultsId;
import static junit5.results.model.TestSuiteResultsMetadataBuilder.aTestSuiteResultsMetadata;
import static org.assertj.core.api.Assertions.assertThat;

public class ResultsExtensionComponentTest {
    @BeforeEach
    void setUp() {
        ResultsExtension.reset();
    }

    @Test
    void resultsIdContainsCorrectClassInformation() {
        TestLauncher.launch(ClassUnderTest.class);
        TestSuiteResults testSuiteResults = ResultsExtension.getAllResults().getClassNameToClassResults().get("ClassUnderTest");
        assertThat(testSuiteResults.getResultsId()).isEqualTo(
            new TestSuiteResultsId(
                "junit5.results.undertest.ClassUnderTest",
                "ClassUnderTest",
                "junit5.results.undertest")
        );
    }

    @Test
    void resultsForPassingTestCases() {
        Class<?> clazz = ClassUnderTest.class;
        TestSuiteResults testSuiteResults = launchTestSuite(clazz);
        assertResultsId(testSuiteResults, clazz);

        TestSuiteResultsMetadata metadata = aTestSuiteResultsMetadata()
            .withTestCaseCount(4)
            .withPassedCount(4)
            .build();
        assertThat(testSuiteResults.getResultsMetadata()).isEqualTo(metadata);

        assertThat(testSuiteResults.getMethodNames()).containsExactlyInAnyOrder(
            "testMethod",
            "paramTest",
            "paramTest",
            "paramTest"
        );

        assertThat(testSuiteResults.getCapturedTestMethod("testMethod")).isEqualTo(passedTestMethod());

        List<TestCaseResult> paramTest = testSuiteResults.getCapturedTestMethods("paramTest");
        assertThat(paramTest.get(0)).isEqualTo(passedParamTestCaseResult("Assert that value 1 is not null"));
        assertThat(paramTest.get(1)).isEqualTo(passedParamTestCaseResult("Assert that value 2 is not null"));
        assertThat(paramTest.get(2)).isEqualTo(passedParamTestCaseResult("Assert that value 3 is not null"));
    }

    @Test
    void resultsForDisabledTestCases() {
        Class<?> clazz = DisabledTestCasesUnderTest.class;
        TestSuiteResults testSuiteResults = launchTestSuite(clazz);
        assertResultsId(testSuiteResults, clazz);

        TestSuiteResultsMetadata metadata = aTestSuiteResultsMetadata()
            .withTestCaseCount(2)
            .withSkippedCount(2)
            .build();
        assertThat(testSuiteResults.getResultsMetadata()).isEqualTo(metadata);

        assertThat(testSuiteResults.getMethodNames()).containsExactlyInAnyOrder(
            "testMethod",
            "paramTest"
        );
    }

    @Test
    void resultsForFailedTestCases() {
        Class<?> clazz = FailedTestCasesUnderTest.class;
        TestSuiteResults testSuiteResults = launchTestSuite(clazz);
        assertResultsId(testSuiteResults, clazz);

        TestSuiteResultsMetadata metadata = aTestSuiteResultsMetadata()
            .withTestCaseCount(4)
            .withFailedCount(4)
            .build();
        assertThat(testSuiteResults.getResultsMetadata()).isEqualTo(metadata);

        assertThat(testSuiteResults.getMethodNames()).containsExactlyInAnyOrder(
            "testMethod",
            "paramTest",
            "paramTest",
            "paramTest"
        );

        TestCaseResult testMethod = testSuiteResults.getCapturedTestMethod("testMethod");
        assertThat(testMethod).isEqualTo(failedTestMethod());
        assertCause(testMethod.getCause(), "\nExpecting:\n <\"testMethod\">\nnot to be equal to:\n <\"testMethod\">\n");

        List<TestCaseResult> paramTest = testSuiteResults.getCapturedTestMethods("paramTest");
        assertThat(paramTest.get(0)).isEqualTo(failedParamTestCaseResult("Assert that value 1 is null"));
        assertCause(paramTest.get(0).getCause(), "\nExpecting:\n <\"value 1\">\nto be equal to:\n <null>\nbut was not.");
        assertThat(paramTest.get(1)).isEqualTo(failedParamTestCaseResult("Assert that value 2 is null"));
        assertCause(paramTest.get(1).getCause(), "\nExpecting:\n <\"value 2\">\nto be equal to:\n <null>\nbut was not.");
        assertThat(paramTest.get(2)).isEqualTo(failedParamTestCaseResult("Assert that value 3 is null"));
        assertCause(paramTest.get(2).getCause(), "\nExpecting:\n <\"value 3\">\nto be equal to:\n <null>\nbut was not.");
    }

    @Test
    void resultsForFailedDueToNullPointerTestCases() {
        Class<?> clazz = ExceptionThrownTestCasesUnderTest.class;
        TestSuiteResults testSuiteResults = launchTestSuite(clazz);
        assertResultsId(testSuiteResults, clazz);

        TestSuiteResultsMetadata metadata = aTestSuiteResultsMetadata()
            .withTestCaseCount(4)
            .withFailedCount(4)
            .build();
        assertThat(testSuiteResults.getResultsMetadata()).isEqualTo(metadata);

        assertThat(testSuiteResults.getMethodNames()).containsExactlyInAnyOrder(
            "testMethod",
            "paramTest",
            "paramTest",
            "paramTest"
        );
    }

    @Test
    void resultsForAbortedTestCases() {
        Class<?> clazz = AbortedTestCasesUnderTest.class;
        TestSuiteResults testSuiteResults = launchTestSuite(clazz);
        assertResultsId(testSuiteResults, clazz);

        TestSuiteResultsMetadata metadata = aTestSuiteResultsMetadata()
            .withTestCaseCount(4)
            .withAbortedCount(4)
            .build();
        assertThat(testSuiteResults.getResultsMetadata()).isEqualTo(metadata);

        assertThat(testSuiteResults.getMethodNames()).containsExactlyInAnyOrder(
            "testMethod",
            "paramTest",
            "paramTest",
            "paramTest"
        );
    }

    private void assertCause(Throwable cause, String message) {
        assertThat(cause.getMessage()).isEqualTo(message);
        assertThat(cause.getClass()).isNotNull();
        assertThat(cause.getCause()).isNull();
        assertThat(cause.getStackTrace()).isNotNull();
    }

    private void assertResultsId(TestSuiteResults testSuiteResults, Class<?> clazz) {
        assertThat(testSuiteResults.getResultsId().getClassName()).isEqualTo(clazz.getSimpleName());
        assertThat(testSuiteResults.getResultsId().getName()).isEqualTo(clazz.getPackage().getName() + "." + clazz.getSimpleName());
        assertThat(testSuiteResults.getResultsId().getPackageName()).isEqualTo(clazz.getPackage().getName());
    }

    private TestSuiteResults launchTestSuite(Class<?> clazz) {
        TestLauncher.launch(clazz);
        assertThat(ResultsExtension.getAllResults().getClasses()).containsExactly(clazz.getSimpleName());
        return ResultsExtension.getAllResults().getClassNameToClassResults().get(clazz.getSimpleName());
    }

    private TestCaseResult passedParamTestCaseResult(String wordify) {
        return aTestCaseResult()
            .withMethodName("paramTest")
            .withWordify(wordify)
            .withStatus(PASSED)
            .withTestSuiteResultsId(testSuiteResultsId(ClassUnderTest.class))
            .build();
    }

    private TestCaseResult failedParamTestCaseResult(String wordify) {
        return aTestCaseResult()
            .withMethodName("paramTest")
            .withWordify(wordify)
            .withStatus(FAILED)
            .withTestSuiteResultsId(testSuiteResultsId(FailedTestCasesUnderTest.class))
            .build();
    }

    private TestCaseResult passedTestMethod() {
        return aTestCaseResult()
            .withMethodName("testMethod")
            .withWordify("Assert that \"test method\" is equal to \"test method\"")
            .withStatus(PASSED)
            .withTestSuiteResultsId(testSuiteResultsId(ClassUnderTest.class))
            .build();
    }

    private TestCaseResult failedTestMethod() {
        return aTestCaseResult()
            .withMethodName("testMethod")
            .withWordify("Assert that \"test method\" is not equal to \"test method\"")
            .withStatus(FAILED)
            .withTestSuiteResultsId(testSuiteResultsId(FailedTestCasesUnderTest.class))
            .build();
    }
}
