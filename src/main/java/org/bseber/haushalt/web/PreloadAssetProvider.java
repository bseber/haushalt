package org.bseber.haushalt.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bseber.haushalt.web.html.PreloadLink;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Component
class PreloadAssetProvider implements ModelAttributeHandlerInterceptor {

    private final AssetManifestService assetManifestService;

    PreloadAssetProvider(AssetManifestService assetManifestService) {
        this.assetManifestService = assetManifestService;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, ModelAndView modelAndView) {
        if (shouldAddAttributes(modelAndView)) {
            final Map<String, List<PreloadLink>> assets = assetManifestService.getAssets(request.getContextPath())
                .entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, o -> toPreloadAsset(o.getValue())));

            modelAndView.getModelMap().addAttribute("assets", assets);
        }
    }

    private static List<PreloadLink> toPreloadAsset(Asset asset) {
        return asset.getDependencies().stream().map(d -> new PreloadLink("script", d)).toList();
    }
}
