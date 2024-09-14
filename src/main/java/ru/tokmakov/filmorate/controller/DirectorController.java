package ru.tokmakov.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.filmorate.model.Director;
import ru.filmorate.service.director.DirectorService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    //=========================/GET/===========================//

    @GetMapping("/directors")
    public Collection<Director> getAll() {
        log.info("==> GET /directors ");
        Collection<Director> allDirectors = directorService.getAll();
        log.info("<== GET /users Список режиcсеров размером: "
                + allDirectors.size() + " возвращен");
        return allDirectors;
    }

    @GetMapping("/directors/{id}")
    public Director get(@PathVariable int id) {
        log.info("==> GET /directors/" + id);
        Director director = directorService.getById(id);
        log.info("<== GET /directors/" + id + "  Режиссер: " + director);
        return director;
    }

    //=========================/POST/===========================//

    @PostMapping("/directors")
    public Director save(@RequestBody Director director) {
        log.info("==> POST /directors " + director);
        Director newDirector = directorService.save(director);
        log.info("<== POST /directors " + newDirector);
        return newDirector;
    }

    //=========================/PUT/===========================//
    @PutMapping("/directors")
    public Director update(@RequestBody Director director) {
        log.info("==> PUT /directors " + director);
        Director updatedDirector = directorService.update(director);
        log.info("<== PUT /directors" + updatedDirector);
        return updatedDirector;
    }

    //=========================/DELETE/===========================//

    @DeleteMapping("/directors/{id}")
    public void delete(@PathVariable int id) {
        log.info("==> DELETE /directors/" + id);
        directorService.deleteById(id);
        log.info("<== DELETE /directors/" + id + "  Режиссер с id=" + id + " удален");
    }

}
