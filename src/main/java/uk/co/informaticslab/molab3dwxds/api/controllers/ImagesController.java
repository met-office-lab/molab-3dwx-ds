package uk.co.informaticslab.molab3dwxds.api.controllers;

import com.theoryinpractise.halbuilder.api.Representation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.informaticslab.molab3dwxds.api.params.DTRange;
import uk.co.informaticslab.molab3dwxds.api.params.ForecastTimeRange;
import uk.co.informaticslab.molab3dwxds.api.representations.MyRepresentationFactory;
import uk.co.informaticslab.molab3dwxds.api.utils.UriResolver;
import uk.co.informaticslab.molab3dwxds.domain.Constants;
import uk.co.informaticslab.molab3dwxds.domain.Image;
import uk.co.informaticslab.molab3dwxds.services.impl.AdvancedMongoMediaService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * Controller class for the images endpoint
 */
@Path(ModelsController.MODELS
        + "/{" + ModelController.MODEL + "}"
        + "/{" + ForecastReferenceTimeController.FORECAST_REFERENCE_TIME + "}"
        + "/{" + PhenomenonController.PHENOMENON + "}"
        + "/{" + ProcessingProfileController.PROCESSING_PROFILE + "}"
        + "/" + ImagesController.IMAGES)
public class ImagesController extends BaseHalController {

    public static final String IMAGES = "images";

    private static final Logger LOG = LoggerFactory.getLogger(ImagesController.class);

    private final AdvancedMongoMediaService mediaService;
    private final String model;
    private final DateTime forecastReferenceTime;
    private final String phenomenon;
    private final String processingProfile;

    @Autowired
    public ImagesController(MyRepresentationFactory representationFactory,
                            UriResolver uriResolver,
                            AdvancedMongoMediaService mediaService,
                            @PathParam(ModelController.MODEL) String model,
                            @PathParam(ForecastReferenceTimeController.FORECAST_REFERENCE_TIME) DateTime forecastReferenceTime,
                            @PathParam(PhenomenonController.PHENOMENON) String phenomenon,
                            @PathParam(ProcessingProfileController.PROCESSING_PROFILE) String processingProfile) {
        super(representationFactory, uriResolver);
        this.mediaService = mediaService;
        this.model = model;
        this.forecastReferenceTime = forecastReferenceTime;
        this.phenomenon = phenomenon;
        this.processingProfile = processingProfile;
    }

    @GET
    @Produces(RepresentationFactory.HAL_JSON)
    public Response get(@Context HttpServletRequest request,
                        @BeanParam ForecastTimeRange forecastTimeRange) {

        LOG.debug("{} = {}", Constants.FORECAST_TIME, forecastTimeRange);

        Iterable<Image> images = mediaService.findAllImageMetaByFilter(model, forecastReferenceTime, phenomenon, processingProfile, forecastTimeRange);

        Representation repr;
        if (!forecastTimeRange.isDateRangeSet()) {
            repr = getCapabilities();
        } else {
            repr = representationFactory.newRepresentation(request.getRequestURI() + "?" + request.getQueryString());
        }
        for (Image image : images) {
            repr.withRepresentation(IMAGES, representationFactory.getImageAsRepresentation(uriResolver.mkUri(MediaController.MEDIA), image));
        }
        return Response.ok(repr).build();
    }


    @Override
    public Representation getCapabilities() {
        Representation repr = representationFactory.newRepresentation(getSelf());
        repr.withLink("get_by_filter", buildFilterURI());
        return repr;
    }

    @Override
    public URI getSelf() {
        return uriResolver.mkUri(ModelsController.MODELS, model, forecastReferenceTime, phenomenon, processingProfile, IMAGES);
    }

    private String buildFilterURI() {
        return new StringBuilder()
                .append(getSelf())
                .append("{?")
                .append(Constants.FORECAST_TIME + "_" + DTRange.GT)
                .append("," + Constants.FORECAST_TIME + "_" + DTRange.GTE)
                .append("," + Constants.FORECAST_TIME + "_" + DTRange.LT)
                .append("," + Constants.FORECAST_TIME + "_" + DTRange.LTE)
                .append("}")
                .toString();
    }

}
