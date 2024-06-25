package com.hits.open.world.rest.controller.note;

import com.hits.open.world.core.note.NoteService;
import com.hits.open.world.public_interface.note.CommonNoteDto;
import com.hits.open.world.public_interface.note.CreateNoteDto;
import com.hits.open.world.public_interface.note.NoteDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note")
@SecurityRequirement(name = "oauth2")
@Tag(name = "Note")
public class NoteController {
    private final NoteService noteService;

    @GetMapping("/all")
    public List<CommonNoteDto> getAllNotes(JwtAuthenticationToken token) {
        var userId = token.getToken().getClaim("sub").toString();
        return noteService.getAllNotes(userId);
    }

    @GetMapping("/{noteId}")
    public NoteDto getNoteById(@PathVariable Long noteId) {
        return noteService.getNoteById(noteId);
    }

    @PostMapping
    public Long createNote(@RequestParam String text,
                           @RequestParam String latitude,
                           @RequestParam String longitude,
                           @RequestParam(required = false) Optional<List<MultipartFile>> images,
                           JwtAuthenticationToken token) {
        var userId = token.getToken().getClaim("sub").toString();
        var createNoteDto = new CreateNoteDto(
                text,
                latitude,
                longitude,
                images.orElse(List.of()),
                userId
        );
        return noteService.createNote(createNoteDto);
    }
}
