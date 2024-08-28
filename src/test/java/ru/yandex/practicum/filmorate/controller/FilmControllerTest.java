//package ru.yandex.practicum.filmorate.controller;
//
////import lombok.RequiredArgsConstructor;
////import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
////import org.springframework.boot.test.context.SpringBootTest;
////import org.springframework.context.annotation.ImportResource;
////import ru.yandex.practicum.filmorate.model.Film;
////import ru.yandex.practicum.filmorate.model.Mpa;
//import ru.yandex.practicum.filmorate.storage.FilmStorage;
//
////import java.time.LocalDate;
////import java.util.Optional;
//
////import static org.assertj.core.api.Assertions.assertThat;
////@SpringBootTest
////@ImportResource
////@AutoConfigureTestDatabase
////@RequiredArgsConstructor(onConstructor_ = @Autowired)
//class FilmRepositoryTest {
//    @Autowired
//    FilmStorage filmStorage;
//
////    @Test
////    void shouldCanFilmAdd() {
////        Film newFilm = new Film();
////        newFilm.setName("film8");
////        newFilm.setDescription("description8");
////        newFilm.setReleaseDate(LocalDate.of(2014, 1, 1));
////        newFilm.setDuration(180);
////        newFilm.setMpa(new Mpa(1L, "G"));
////
////        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.addFilm(newFilm));
////        assertThat(filmOptional)
////                .isPresent()
////                .hasValueSatisfying(film -> {
////                            assertThat(film).hasFieldOrPropertyWithValue("id", 8L);
////                            assertThat(film).hasFieldOrPropertyWithValue("name", "film8");
////                            assertThat(film).hasFieldOrPropertyWithValue("description", "description8");
////                            assertThat(film).hasFieldOrPropertyWithValue("duration", 180);
////                            assertThat(film).hasFieldOrPropertyWithValue("releaseDate",
////                                    LocalDate.of(2014, 1, 1));
////                            assertThat(film.getMpa()).hasFieldOrPropertyWithValue("id", 1L);
////                        }
////                );
////
////    }
//
//
//}