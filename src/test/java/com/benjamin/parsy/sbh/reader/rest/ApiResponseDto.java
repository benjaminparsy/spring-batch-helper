package com.benjamin.parsy.sbh.reader.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto {

    @JsonProperty(value = "page")
    private Integer page;

    @JsonProperty(value = "per_page")
    private Integer perPage;

    @JsonProperty(value = "total")
    private Integer total;

    @JsonProperty(value = "total_pages")
    private Integer totalPages;

    @JsonProperty(value = "data")
    private List<DataResponseDto> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataResponseDto {

        @JsonProperty(value = "id")
        private Long id;

        @JsonProperty(value = "email")
        private String email;

        @JsonProperty(value = "first_name")
        private String firstname;

        @JsonProperty(value = "last_name")
        private String lastname;

        @JsonProperty(value = "avatar")
        private String avatar;

    }

}