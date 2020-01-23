/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms

import forms.mappings.Mappings
import com.google.inject.Inject
import play.api.data.Form

class UTRFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("utr.error.required")
        .verifying(
          firstError(
            maxLength(10, "utr.error.length"),
            minLength(10, "utr.error.length"),
            regexp(Validation.utrRegex, "utr.error.invalidCharacters"),
            isNotEmpty("value", "utr.error.required")
          ))
    )
}

