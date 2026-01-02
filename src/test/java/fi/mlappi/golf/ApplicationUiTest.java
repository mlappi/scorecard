package fi.mlappi.golf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApplicationUiTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homePageDisplaysNavigationLinks() {
        ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotBlank();
        assertThat(response.getBody()).contains("Golf Skin Games");

        Document document = Jsoup.parse(response.getBody());
        assertThat(document.select("a.nav-link[href='/game']")).isNotEmpty();
        assertThat(document.select("a.nav-link[href='/admin/login']")).isNotEmpty();
    }

    @Test
    void adminLoginPageShowsCredentialsForm() {
        ResponseEntity<String> response = restTemplate.getForEntity("/admin/login", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Document document = Jsoup.parse(response.getBody());
        assertThat(document.select("form[action='/admin/login']")).hasSize(1);
        assertThat(document.select("input[name='username']")).hasSize(1);
        assertThat(document.select("input[name='password']")).hasSize(1);
        assertThat(document.select("button[type='submit']")).isNotEmpty();
    }

    @Test
    void adminCanLoginAndSeeLogoutLink() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/admin/login")
                        .param("username", "admin")
                        .param("password", "admin123"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertThat(session).isNotNull();
        assertThat(session.getAttribute("isAdmin")).isEqualTo(Boolean.TRUE);

        mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Kirjaudu ulos")));
    }
}
