package com.meshapi.sdk.types.audio;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PronunciationDictionaryLocator {
    @JsonProperty("pronunciation_dictionary_id") public String pronunciationDictionaryId;
    @JsonProperty("version_id") public String versionId;
}
