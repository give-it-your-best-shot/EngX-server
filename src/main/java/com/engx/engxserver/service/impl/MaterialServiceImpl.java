package com.engx.engxserver.service.impl;

import com.engx.engxserver.dto.AddBookRequestDTO;
import com.engx.engxserver.dto.AddUnitRequestDTO;
import com.engx.engxserver.dto.AddWordRequestDTO;
import com.engx.engxserver.dto.BookDTO;
import com.engx.engxserver.dto.UnitDTO;
import com.engx.engxserver.dto.WordDTO;
import com.engx.engxserver.entity.Book;
import com.engx.engxserver.entity.Unit;
import com.engx.engxserver.entity.User;
import com.engx.engxserver.entity.Word;
import com.engx.engxserver.exception.InsertFailException;
import com.engx.engxserver.repository.BookRepository;
import com.engx.engxserver.repository.UnitRepository;
import com.engx.engxserver.repository.UserRepository;
import com.engx.engxserver.repository.WordRepository;
import com.engx.engxserver.service.base.MaterialService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {
    private final UnitRepository unitRepository;
    private final BookRepository bookRepository;
    private final WordRepository wordRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final MessageSource messageSource;

    @Override
    public UnitDTO addUnit(AddUnitRequestDTO addUnitRequestDTO) throws InsertFailException {
        Optional<Book> book = bookRepository.findById(addUnitRequestDTO.getBookId());
        Unit unit = new Unit();
        unit.setName(addUnitRequestDTO.getName());
        unit.setBook(book.get());
        Unit savedUnit = unitRepository.save(unit);
        UnitDTO unitDTO = modelMapper.map(savedUnit, UnitDTO.class);
        return unitDTO;
    }

    @Override
    public List<UnitDTO> getAllUnitsOfBook(Long bookId) {
        return unitRepository.findAllByBook(bookId).stream()
                .map(unit -> modelMapper.map(unit, UnitDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<WordDTO> getAllWordsOfUnit(Long unitId) {
        return wordRepository.findAllByUnit(unitId).stream()
                .map(word -> modelMapper.map(word, WordDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDTO> getAllBooksOfOwner(Long ownerId) {
        return bookRepository.findAllByOwner(ownerId).stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public WordDTO addWord(AddWordRequestDTO addWordRequestDTO) throws InsertFailException {
        Word word = new Word();
        word.setMeaning(addWordRequestDTO.getMeaning());
        word.setWriting(addWordRequestDTO.getWriting());
        Optional<Unit> unit = unitRepository.findById(addWordRequestDTO.getUnitId());
        word.setUnit(unit.get());
        Word savedWord = wordRepository.save(word);
        return modelMapper.map(savedWord, WordDTO.class);
    }

    @Override
    public BookDTO addBook(AddBookRequestDTO addBookRequestDTO) throws InsertFailException {
        Book book = new Book();
        book.setName(addBookRequestDTO.getName());
        Optional<User> user = userRepository.findById(addBookRequestDTO.getOwnerId());
        book.setOwner(user.get());
        Book savedBook = bookRepository.save(book);
        return modelMapper.map(savedBook, BookDTO.class);
    }

    @Override
    public BookDTO getBookById(Long bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        List<UnitDTO> unitDTOs = book.get().getUnits().stream()
                .map(unit -> {
                    UnitDTO unitDTO = new UnitDTO();
                    unitDTO.setId(unit.getId());
                    unitDTO.setName(unit.getName());
                    return unitDTO;
                })
                .collect(Collectors.toList());
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(book.get().getId());
        bookDTO.setName(book.get().getName());
        bookDTO.setDescription(book.get().getDescription());
        bookDTO.setPhotoUrl(book.get().getPhotoUrl());
        bookDTO.setUnits(unitDTOs);
        return bookDTO;
    }

    @Override
    public UnitDTO getUnitById(Long unitId) {
        Optional<Unit> unit = unitRepository.findById(unitId);
        return modelMapper.map(unit.get(), UnitDTO.class);
    }

    @Override
    public List<BookDTO> findBookWithNameContain(String name) {
        List<Book> books = bookRepository.findBookWithNameContain(name);
        List<BookDTO> bookDTOs = new ArrayList<>();
        for (Book book : books) {
            BookDTO bookDTO = new BookDTO();
            bookDTO.setId(book.getId());
            bookDTO.setName(book.getName());
            bookDTO.setDescription(book.getDescription());
            bookDTO.setPhotoUrl(book.getPhotoUrl());
            bookDTOs.add(bookDTO);
        }
        return bookDTOs;
    }

    @Override
    public List<BookDTO> findAllAdminBooks() {
        List<Book> books = bookRepository.findAllAdminBooks();
        List<BookDTO> bookDTOs = new ArrayList<>();
        for (Book book : books) {
            BookDTO bookDTO = new BookDTO();
            bookDTO.setId(book.getId());
            bookDTO.setName(book.getName());
            bookDTO.setDescription(book.getDescription());
            bookDTO.setPhotoUrl(book.getPhotoUrl());
            bookDTOs.add(bookDTO);
        }
        return bookDTOs;
    }
}
