package com.patika.kredinbizdeservice.client.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ApplicationResponse {

    private Long userId;
    private LocalDateTime createDate;
    private ApplicationStatus applicationStatus;

    public void setApplicationId(long l) {
        l = Long.valueOf(l);
    }
}
