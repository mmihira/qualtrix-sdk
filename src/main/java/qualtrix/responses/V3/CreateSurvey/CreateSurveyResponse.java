package qualtrix.responses.V3.CreateSurvey;


import lombok.*;
import qualtrix.responses.V3.BaseResponse;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CreateSurveyResponse extends BaseResponse {
    @NonNull
    public CreateSurveyResponseResult result;
}
