package com.hits.open.world.rest.controller.quest;

import com.hits.open.world.core.quest.QuestService;
import com.hits.open.world.core.quest.repository.entity.quest.QuestType;
import com.hits.open.world.core.quest.repository.entity.quest.TransportType;
import com.hits.open.world.public_interface.quest.AllQuestDto;
import com.hits.open.world.public_interface.quest.CommonQuestDto;
import com.hits.open.world.public_interface.quest.CompletedQuestDto;
import com.hits.open.world.public_interface.quest.CreateDistanceQuestDto;
import com.hits.open.world.public_interface.quest.CreatePointToPointQuestDto;
import com.hits.open.world.public_interface.quest.CreateQuestDto;
import com.hits.open.world.public_interface.quest.DistanceQuestDto;
import com.hits.open.world.public_interface.quest.GetQuestsDto;
import com.hits.open.world.public_interface.quest.PointToPointQuestDto;
import com.hits.open.world.public_interface.quest.StartQuestDto;
import com.hits.open.world.public_interface.quest.UpdateQuestDto;
import com.hits.open.world.public_interface.quest.review.AddImageQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.CreateQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.DeleteImageQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.DeleteQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.UpdateQuestReviewDto;
import com.hits.open.world.public_interface.route.CreateRouteDto;
import com.hits.open.world.public_interface.route.PointDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.service.annotation.DeleteExchange;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quest")
@SecurityRequirement(name = "oauth2")
@Tag(name = "Quest")
public class QuestController {
    private final QuestService questService;

    @PostMapping(path = "/point_to_point")
    public void createPointToPointQuest(@RequestParam("name") String name,
                                        @RequestParam("description") String description,
                                        @RequestParam("difficulty_type") String difficultyType,
                                        @RequestParam("transport_type") String transportType,
                                        @RequestParam("images") List<MultipartFile> images,
                                        @RequestParam("points") List<PointDto> points,
                                        @RequestParam("start_point_longitude") String startPointLongitude,
                                        @RequestParam("start_point_latitude") String startPointLatitude) {
        var createDto = new CreateQuestDto(
                name,
                description,
                difficultyType,
                QuestType.POINT_TO_POINT.name(),
                transportType,
                images
        );
        var routeDto = new CreateRouteDto(
                points,
                startPointLongitude,
                startPointLatitude
        );
        questService.createPointToPointQuest(new CreatePointToPointQuestDto(createDto, routeDto));
    }

    @PostMapping(path = "/distance")
    public void createDistanceQuest(@RequestParam("name") String name,
                                    @RequestParam("description") String description,
                                    @RequestParam("difficulty_type") String difficultyType,
                                    @RequestParam("transport_type") String transportType,
                                    @RequestParam("images") List<MultipartFile> images,
                                    @RequestParam("distance") Double distance,
                                    @RequestParam("longitude") String longitude,
                                    @RequestParam("latitude") String latitude) {
        var createDto = new CreateQuestDto(
                name,
                description,
                difficultyType,
                QuestType.DISTANCE.name(),
                transportType,
                images
        );
        questService.createDistanceQuest(new CreateDistanceQuestDto(
                createDto,
                distance,
                longitude,
                latitude
        ));
    }

    @DeleteMapping
    public void deleteQuest(@RequestParam("quest_id") Long questId) {
        questService.deleteQuest(questId);
    }

    @PostMapping(path = "/{quest_id}/start")
    public void startQuest(@PathVariable("quest_id") Long questId,
                           @RequestParam(value = "transport_type", required = false) Optional<String> transportType,
                           JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        var startDto = new StartQuestDto(
                questId,
                userId,
                transportType.orElse(TransportType.WALK.name())
        );
        questService.startQuest(startDto);
    }

    @PostMapping(path = "/{quest_id}/finish")
    public void finishQuest(@PathVariable("quest_id") Long questId,
                            JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        questService.finishQuest(questId, userId);
    }

    @DeleteMapping(path = "/{quest_id}/cancel")
    public void cancelQuest(@PathVariable("quest_id") Long questId,
                            JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        questService.cancelQuest(questId, userId);
    }

    @PatchMapping(path = "/{quest_id}")
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

    @PostMapping(path = "/{quest_id}/image")
    public void addQuestImage(@PathVariable("quest_id") Long questId,
                              @RequestParam("image") MultipartFile image) {
        questService.saveQuestImage(image, questId);
    }

    @DeleteMapping(path = "/{quest_id}/image")
    public void removeQuestImage(@PathVariable("quest_id") Long questId,
                                 @RequestParam("image_id") Long imageId) {
        questService.deleteQuestImage(questId, imageId);
    }

    @PostMapping(path = "/{quest_id}/review")
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

    @DeleteMapping(path = "/{quest_id}/review")
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

    @PatchMapping(path = "/{quest_id}/review/{review_id}")
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

    @PostMapping(path = "/{quest_id}/review/{review_id}/image")
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

    @DeleteMapping(path = "/{quest_id}/review/{review_id}/image")
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

    @GetMapping(path = "/list")
    public AllQuestDto getQuestsList(
            @RequestParam(value = "name", required = false) Optional<String> name,
            JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        var dto = new GetQuestsDto(
                name.orElse(""),
                userId
        );
        return questService.getQuests(dto);
    }

    @GetMapping(path = "/my/completed")
    public List<CompletedQuestDto> getMyCompletedQuests(JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return questService.getMyCompletedQuests(userId);
    }

    @GetMapping(path = "/my/active")
    public List<CommonQuestDto> getMyActiveQuests(JwtAuthenticationToken token) {
        var userId = token.getTokenAttributes().get("sub").toString();
        return questService.getMyActiveQuests(userId);
    }

    @GetMapping(path = "/point_to_point/{quest_id}")
    public PointToPointQuestDto getPointToPointQuest(@PathVariable("quest_id") Long questId) {
        return questService.getPointToPointQuest(questId);
    }

    @GetMapping(path = "/distance/{quest_id}")
    public DistanceQuestDto getDistanceQuest(@PathVariable("quest_id") Long questId) {
        return questService.getDistanceQuest(questId);
    }
}
