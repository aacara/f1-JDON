package kernel.jdon.favorite.controller;

import java.net.URI;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kernel.jdon.auth.dto.SessionUserInfo;
import kernel.jdon.dto.response.CommonResponse;
import kernel.jdon.favorite.dto.request.UpdateFavoriteRequest;
import kernel.jdon.favorite.dto.response.FindListFavoriteResponse;
import kernel.jdon.favorite.dto.response.UpdateFavoriteResponse;
import kernel.jdon.favorite.service.FavoriteService;
import kernel.jdon.global.annotation.LoginUser;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FavoriteController {

	private final FavoriteService favoriteService;

	@GetMapping("/api/v1/favorites")
	public ResponseEntity<CommonResponse> getList(@LoginUser SessionUserInfo user,
		@PageableDefault(size = 12) Pageable pageable) {
		FindListFavoriteResponse findListFavoriteResponse = favoriteService.findList(user.getId(), pageable);

		return ResponseEntity.ok(CommonResponse.of(findListFavoriteResponse.getCustomPageInfo()));
	}

	@PostMapping("/api/v1/favorites")
	public ResponseEntity<CommonResponse> update(@LoginUser SessionUserInfo user,
		@RequestBody UpdateFavoriteRequest updateFavoriteRequest) {
		UpdateFavoriteResponse updateFavoriteResponse = favoriteService.update(user.getId(),
			updateFavoriteRequest);
		URI uri = URI.create("/api/v1/favorites/" + updateFavoriteResponse.getLectureId());

		return ResponseEntity.created(uri).body(CommonResponse.of(updateFavoriteResponse));
	}
}
