package com.dev.usersmanagementsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class EmailDetails {
    private final List<String> recipient;

    private final List<String> cc;

    private final List<String> bcc;

    private final String subject;

    private final String body;

    private final String attachment;
}
