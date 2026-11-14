# PrintXpress Mobile App — Report Test Evidence

**Source:** `st20345403_CSE5011_WRIT1.docx`, CSE5011 — Mobile Application Development.
**Evidence classification:** Historic screenshots and recorded outcomes from the submitted academic report — not freshly executed tests.

**Public privacy review:** Only a subset of test captures is published here. Other captures remain in the original academic report to avoid exposing account details or payment-form examples.

| Test | Reported scenario | Reported actual outcome | Original report screenshot |
| --- | --- | --- | --- |
| TC01 | User Registration — valid details | Registration successful | Retained in original report (not publicly uploaded) |
| TC02 | User Registration — required fields | Validation displayed | Retained in original report (not publicly uploaded) |
| TC03 | User Registration — invalid email | Validation displayed | Retained in original report (not publicly uploaded) |
| TC04 | User Registration — confirmation mismatch | Validation displayed | Retained in original report (not publicly uploaded) |
| TC05 | User Login — valid account | Login successful | Retained in original report (not publicly uploaded) |
| TC06 | User Login — incorrect password | Error displayed | Retained in original report (not publicly uploaded) |
| TC07 | Product Browsing | Products displayed | Original report capture withheld from public repository |
| TC08 | Product Details | Details displayed | Original report capture withheld from public repository |
| TC09 | Product Search | Product displayed | Original report capture withheld from public repository |
| TC10 | Place Order — valid input | Order created | Original report capture withheld from public repository |
| TC11 | Place Order — invalid quantity | Validation displayed | Original report capture withheld from public repository |
| TC12 | Design Upload | Upload successful | Original report capture withheld from public repository |
| TC13 | Payment (local card-validation simulation) | Report states payment successful; no real gateway is implemented | Retained in original report (not publicly uploaded) |
| TC14 | Order Tracking | Status displayed | Original report capture withheld from public repository |
| TC15 | Order Management — cancellation | Report states order cancelled | Original report capture withheld from public repository |
| TC16 | Profile Management | Updated successfully | Retained in original report (not publicly uploaded) |
| TC17 | Notifications | Notifications displayed | Original report capture withheld from public repository |
| TC18 | Database — save order | Data saved | Retained in original report (not publicly uploaded) |
| TC19 | Database — retrieve order | Data retrieved | Retained in original report (not publicly uploaded) |
| TC20 | Navigation between screens | Navigation successful | Original report capture withheld from public repository |

## What these captures do and do not establish

- They are PNG image assets embedded in the original coursework DOCX report. Source media filenames and SHA-256 hashes are in [`SOURCE_PROVENANCE.json`](SOURCE_PROVENANCE.json).
- They support that these screens and outcomes were **documented** in the submitted report.
- They do **not** establish that a fresh build, a fresh integration test run, or an independent QA review passed today.
- Published gallery images have not been edited; the raw test screenshots are withheld. Review personally identifying details, contact information, or card-form inputs before publishing.
- Simulated card/payment steps should always be labelled **academic simulations**, never real settled transactions.

## Reproduction guidance

For fresh reproducible evidence, run the original project in a disposable local environment, use fictional test data, capture each use case, and record build/test commands, dates, versions, and expected/actual results. Preserve the original coursework implementation.
