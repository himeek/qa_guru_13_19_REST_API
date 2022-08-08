package tests;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static helpers.CustomApiListener.withCustomTemplates;

public class DemoWebShopTest extends TestBase {


    @Test
    @Tag("TestDemoWebShop")
    @DisplayName("UI. Registration user")
    void registrationTest() {
        registrationPage.openPage()
                .setGenderMale()
                .setFirstName(testData.firstName)
                .setLastName(testData.lastName)
                .setMail(testData.userEmail)
                .setPassword(testData.password)
                .setConfirmPassword(testData.password)
                .clickRegister()
                .checkResult();
    }

    @Test
    @Tag("TestDemoWebShop")
    @DisplayName("API+UI. User authorization")
    void authTest() {
        String cookieKey = "NOPCOMMERCE.AUTH";
        String cookieValue = given()
                .filter(withCustomTemplates())
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .log().all()
                .formParam("Email", testData.email)
                .formParam("Password", testData.password)
                .when()
                .post("http://demowebshop.tricentis.com/login")
                .then()
                .log().all()
                .statusCode(302)
                .extract()
                .cookie(cookieKey);

        open("/login");
        Cookie authCookie = new Cookie(cookieKey, cookieValue);
        getWebDriver().manage().addCookie(authCookie);
        open("");
        $(".account").shouldHave(text(testData.email));
    }

    @Test
    @Tag("TestDemoWebShop")
    @DisplayName("API+UI. Update info user")
    void updateTest() {
        String cookieKey = "NOPCOMMERCE.AUTH";
        String cookieValue = given()
                .filter(withCustomTemplates())
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .log().all()
                .formParam("Email", testData.email)
                .formParam("Password", testData.password)
                .when()
                .post("http://demowebshop.tricentis.com/login")
                .then()
                .log().all()
                .statusCode(302)
                .extract()
                .cookie(cookieKey);
        open("");
        Cookie authCookie = new Cookie(cookieKey, cookieValue);
        getWebDriver().manage().addCookie(authCookie);
        open("/customer/info");
        $("#FirstName").setValue("TestFirstName");
        $("#LastName").setValue("TestLastName");
        $("[for='gender-female']").click();
        $("[name='save-info-button']").click();
        $("#FirstName").shouldHave(Condition.value("TestFirstName"));
    }
}
