package com.hits.open.world.rest.quest;

import com.hits.open.world.core.quest.QuestService;
import com.hits.open.world.public_interface.quest.CreateQuestDto;
import com.hits.open.world.public_interface.quest.UpdateQuestDto;
import com.hits.open.world.public_interface.quest.review.AddImageQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.CreateQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.DeleteImageQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.DeleteQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.UpdateQuestReviewDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

//TODO: придумать механизм добавление дополнительных параметров при создании квеста, так как разыне типы

@RestController
@RequiredArgsConstructor
@RequestMapping("/quest")
@SecurityRequirement(name = "oauth2")
@Tag(name = "Quest")
public class QuestController {
    private final QuestService questService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createQuest(@RequestParam("name") String name,
                            @RequestParam("description") String description,
                            @RequestParam("difficulty_type") String difficultyType,
                            @RequestParam("quest_type") String questType,
                            @RequestParam("transport_type") String transportType,
                            @RequestParam("images")List<MultipartFile> images) {
        var createDto = new CreateQuestDto(
                name,
                description,
                difficultyType,
                questType,
                transportType,
                images
        );
        questService.createQuest(createDto);
    }

    @DeleteMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void deleteQuest(@RequestParam("quest_id") Long questId) {
        questService.deleteQuest(questId);
    }

    @PostMapping(path = "/{quest_id}/start", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void startQuest(@PathVariable("quest_id") Long questId,
                           JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        questService.startQuest(questId, userId);
    }

    @PostMapping(path = "/{quest_id}/finish", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void finishQuest(@PathVariable("quest_id") Long questId,
                            JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        questService.finishQuest(questId, userId);
    }

    @PatchMapping(path = "/{quest_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateQuest(@PathVariable("quest_id") Long questId,
                            @RequestParam("name") Optional<String> name,
                            @RequestParam("description") Optional<String> description,
                            @RequestParam("difficulty_type") Optional<String> difficultyType,
                            @RequestParam("quest_type") Optional<String> questType,
                            @RequestParam("transport_type") Optional<String> transportType) {
        var updateDto = new UpdateQuestDto(
                questId,
                name,
                description,
                difficultyType,
                questType,
                transportType
        );
        questService.updateQuest(updateDto);
    }

    @PostMapping(path = "/{quest_id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addQuestImage(@PathVariable("quest_id") Long questId,
                              @RequestParam("image") MultipartFile image) {
        questService.saveQuestImage(image, questId);
    }

    @DeleteMapping(path = "/{quest_id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void removeQuestImage(@PathVariable("quest_id") Long questId,
                                 @RequestParam("image_id") Long imageId) {
        questService.deleteQuestImage(questId, imageId);
    }

    @PostMapping(path = "/{quest_id}/review", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addQuestReview(@PathVariable("quest_id") Long questId,
                               @RequestParam("score") Integer score,
                               @RequestParam("message") String message,
                               @RequestParam("images") List<MultipartFile> images,
                               JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        var createDto = new CreateQuestReviewDto(
                questId,
                score,
                message,
                userId,
                images
        );
        questService.createQuestReview(createDto);
    }

    @DeleteMapping(path = "/{quest_id}/review", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void removeQuestReview(@PathVariable("quest_id") Long questId,
                                  @RequestParam("review_id") Long reviewId,
                                  JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        var deleteDto = new DeleteQuestReviewDto(
                reviewId,
                questId,
                userId
        );
        questService.deleteQuestReview(deleteDto);
    }

    @PatchMapping(path = "/{quest_id}/review/{review_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateQuestReview(@PathVariable("quest_id") Long questId,
                                  @PathVariable("review_id") Long reviewId,
                                  @RequestParam("score") Optional<Integer> score,
                                  @RequestParam("message") Optional<String> message,
                                  JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        var updateDto = new UpdateQuestReviewDto(
                questId,
                reviewId,
                userId,
                score,
                message
        );
        questService.updateQuestReview(updateDto);
    }

    @PostMapping(path = "/{quest_id}/review/{review_id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addQuestReviewImage(@PathVariable("quest_id") Long questId,
                                    @PathVariable("review_id") Long reviewId,
                                    @RequestParam("image") MultipartFile image,
                                    JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        var addDto = new AddImageQuestReviewDto(
                questId,
                reviewId,
                userId,
                image
        );
        questService.addImageQuestReview(addDto);
    }

    @DeleteMapping(path = "/{quest_id}/review/{review_id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void removeQuestReviewImage(@PathVariable("quest_id") Long questId,
                                       @PathVariable("review_id") Long reviewId,
                                       @RequestParam("image_id") Long imageId,
                                       JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        var deleteDto = new DeleteImageQuestReviewDto(
                questId,
                reviewId,
                userId,
                imageId
        );
        questService.deleteImageQuestReview(deleteDto);
    }
}
