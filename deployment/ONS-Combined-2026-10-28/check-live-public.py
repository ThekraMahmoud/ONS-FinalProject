from concurrent.futures import ThreadPoolExecutor
from datetime import datetime, timezone
from pathlib import Path
import json
import re
import urllib.error
import urllib.request

BASE = "https://d3ujyxwecja6r4.cloudfront.net"
PAGES = ["/", "/login", "/register", "/payment", "/set-password",
         "/dashboard", "/campaign-dashboard", "/supervisor-dashboard",
         "/representative-dashboard", "/pilgrim-kiosk", "/pilgrim-kiosk/setup"]


def check(case):
    method, path, expected = case
    body = b"{}" if method == "POST" and path.endswith("/register") else None
    headers = {"X-Requested-With": "XMLHttpRequest"}
    if body is not None:
        headers["Content-Type"] = "application/json"
    request = urllib.request.Request(BASE + path, data=body, headers=headers, method=method)
    try:
        response = urllib.request.urlopen(request, timeout=20)
    except urllib.error.HTTPError as error:
        response = error
    except Exception as error:
        return {"method": method, "path": path, "expected": expected,
                "passed": False, "error": str(error)}
    with response:
        status = response.status
        content = response.read()
        content_type = response.headers.get("Content-Type", "")
    result = {"method": method, "path": path, "expected": expected, "status": status,
              "content_type": content_type, "bytes": len(content), "passed": status == expected}
    if path in PAGES:
        result["react_shell"] = b'id="root"' in content and b'/assets/' in content
        result["passed"] = result["passed"] and result["react_shell"]
    if status >= 400:
        try:
            payload = json.loads(content)
            result["message"] = str(payload.get("Message") or payload.get("message") or payload.get("error") or "")[:180]
        except Exception:
            pass
    return result


cases = [("GET", path, 200) for path in PAGES]
cases += [("GET", path, 401) for path in [
    "/api/v1/auth/get/all", "/api/v1/dashboard/summary", "/api/v1/campaigns/get",
    "/api/v1/campaigns/me", "/api/v1/pilgrims/get", "/api/v1/pilgrims/me",
    "/api/v1/pilgrim-cards/get", "/api/v1/kiosks/get", "/notification/get", "/report/get"]]
cases += [("POST", "/api/v1/campaigns/register", 400),
          ("POST", "/api/v1/payments/campaign/99999999", 403),
          ("POST", "/api/v1/payments/campaign/99999999/99999999/verify", 403),
          ("GET", "/api/v1/map/kiosks/99999999/nearest-place?type=GATE", 400)]
with ThreadPoolExecutor(max_workers=4) as pool:
    results = list(pool.map(check, cases))

with urllib.request.urlopen(BASE + "/", timeout=20) as response:
    index = response.read().decode()
assets = sorted(set(re.findall(r'(?:src|href)="(/[^\"]+)"', index)))
for path in assets:
    results.append(check(("GET", path, 200)))

output = {"base_url": BASE, "checked_at": datetime.now(timezone.utc).isoformat(),
          "scope": "Public pages, their entry assets, anonymous API guards, invalid registration and payment ownership. No authenticated account, record changes, paid checkout, email, n8n, Python or Google calls.",
          "passed": sum(row["passed"] for row in results),
          "failed": sum(not row["passed"] for row in results), "results": results}
destination = Path(__file__).with_name("live-public-results.json")
destination.write_text(json.dumps(output, indent=2) + "\n")
print(json.dumps(output, indent=2))
