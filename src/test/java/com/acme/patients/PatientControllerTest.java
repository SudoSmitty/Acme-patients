package com.acme.patients;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "acme.release.regression=false")
@AutoConfigureMockMvc
class PatientControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void listsPatients() throws Exception {
        mvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void escapesNotes() throws Exception {
        mvc.perform(get("/api/patients/p-1002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notes").value("Monitoring &lt;pending&gt;"));
    }

    @Test
    void unknownPatientIs404() throws Exception {
        mvc.perform(get("/api/patients/nope")).andExpect(status().isNotFound());
    }
}
