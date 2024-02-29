package com.example.bookadminapp.mapper;

import com.example.bookadminapp.dto.BookDto;
import com.example.bookadminapp.dto.ResponseDto;
import com.example.bookadminapp.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    Book convertToEntity(BookDto dto);

    BookDto convertToDto(Book book);

    @Mapping(target = "categories", ignore = true)
    ResponseDto convertToResponse(Book book);


}
