package fi.mlappi.golf;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = "app.admin.enabled=false")
class PublicReadOnlyUiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicNavigationDoesNotShowAdminLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("/admin/login"))));
    }

    @Test
    void publicProfileDoesNotExposeAdminLogin() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isNotFound());
    }

    @Test
    void publicProfileBlocksMutatingRoutesEvenWithAdminSessionFlag() throws Exception {
        mockMvc.perform(post("/player/save")
                        .sessionAttr("isAdmin", Boolean.TRUE))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/player/new")
                        .sessionAttr("isAdmin", Boolean.TRUE))
                .andExpect(status().isForbidden());
    }

    @Test
    void publicProfileKeepsSearchAvailable() throws Exception {
        mockMvc.perform(post("/player/search")
                        .param("playerName", "Matti"))
                .andExpect(status().isOk());
    }
}
