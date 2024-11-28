package ci.ivb.testing;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test Math operation in Calculator class")
class CalculatorTest {

   Calculator calculator;

   @BeforeAll
   static void setup() {
      System.out.println("Execution @BeforeAll method");
   }

   @BeforeEach
   void beforeEachTestMethod() {
      calculator = new Calculator();
      System.out.println("Execution @BeforeEach method");
   }

   @AfterEach
   void afterEachTestMethod() {
      System.out.println("Execution @AfterEach method");
   }

   @AfterAll
   static void cleanup() {
      System.out.println("Execution @AfterAll method");
   }

   @Test
   @DisplayName("Test division 4/2 = 2")
    void integerDivisionTest() {
      //Arrange //Given
      int dividend = 4;
      int divisor = 2;
      int expectedResult = 2;

      //Act //When
      int resultActual = calculator.integerDivision(dividend, divisor);

      //Assert //Then
      assertEquals(expectedResult, resultActual, "4/2 should not be 2");
   }

//   @Disabled("TODO: Still need to work on it")
//   @RepeatedTest(3)
   @Test
   @DisplayName("Division by zero")
   void divisionByZeroTest() {
      int dividend = 4;
      int divisor = 0;

      assertThrows(ArithmeticException.class, ()-> {
         calculator.integerDivision(dividend, divisor);
      }, "Division by zero should have throw an Arithmetic exception");
   }

   @Test
   @DisplayName("Test substraction 10-5 = 5")
   void integerSubtractionTest() {
      int minuend = 10;
      int subtrahend = 5;
      int expectedResult = 5;

      int actualResult = calculator.integerSubtraction(minuend, subtrahend);

      assertEquals(expectedResult,actualResult);
   }

   @DisplayName("Test substraction with parameters")
   @ParameterizedTest
   @MethodSource("integerSubtractionWithParameterTest")
   void integerSubtractionParametersTest(int minuend, int subtrahend, int expectedResult) {
      int actualResult = calculator.integerSubtraction(minuend, subtrahend);

      assertEquals(expectedResult,actualResult);
   }

   private static Stream<Arguments> integerSubtractionWithParameterTest() {
      return Stream.of(
              Arguments.of(10,5,5),
              Arguments.of(23,11,12),
              Arguments.of(85,9,76)
      );
   }

   @DisplayName("Test substraction with CsvSource")
   @ParameterizedTest
   @CsvSource({
           "14,6,8",
           "10,-5,15",
           "20,20,0",
   })
   void integerSubtractionCsvSourceTest(int minuend, int subtrahend, int expectedResult) {
      int actualResult = calculator.integerSubtraction(minuend, subtrahend);

      assertEquals(expectedResult,actualResult);
   }

   @DisplayName("Test substraction with CsvFileSource")
   @ParameterizedTest
   @CsvFileSource(resources = "/integerSubstraction.csv")
   void integerSubtractionCsvFileSourceTest(int minuend, int subtrahend, int expectedResult) {
      int actualResult = calculator.integerSubtraction(minuend, subtrahend);

      assertEquals(expectedResult,actualResult);
   }

   @DisplayName("Test valueSource parameter")
   @ParameterizedTest
   @ValueSource(strings = {"Konan", "Jean", "Soro"})
   void valueSourceDemonstration(String name) {
      System.out.println(name);
      assertNotNull(name);
   }

}