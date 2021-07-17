package com.example.myhealth.domain;

import lombok.*;


@NoArgsConstructor
@Getter
@Setter
public class ExerciseSetDto {
    private int set;
    private String exerciseTime;
    private String restTime;
}
