package com.meshapi.sdk.types.audio;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoiceSettings {
    @JsonProperty("stability") public Double stability;
    @JsonProperty("similarity_boost") public Double similarityBoost;
    @JsonProperty("style") public Double style;
    @JsonProperty("use_speaker_boost") public Boolean useSpeakerBoost;
    @JsonProperty("speed") public Double speed;
}
