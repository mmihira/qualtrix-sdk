package qualtrix.responses.V3.ResponseExport;

import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
public class CreateResponseExportBodyFilterQuestions extends AbstractCreateResponseExportBody {
  private List<String> questionIds;

  public CreateResponseExportBodyFilterQuestions(@NonNull ResponseExportFormat format) {
    super(format);
  }

  public CreateResponseExportBodyFilterQuestions(
      @NonNull ResponseExportFormat format, List<String> questionIds) {
    super(format);
    this.questionIds = questionIds;
  }
}
