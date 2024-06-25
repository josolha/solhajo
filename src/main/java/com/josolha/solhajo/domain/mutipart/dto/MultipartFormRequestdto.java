package com.josolha.solhajo.domain.mutipart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MultipartFormRequestdto {
   // private MultipartFile file;
    private String title; // 텍스트 데이터
    private String description;
}
