# MaterialTok region preferences

## Scope

This prototype may offer a user-selected **content preference region**. This is a preference stored by MaterialTok; it does not change the Android SIM, carrier registration, MCC/MNC, device location, or TikTok's server-side region signals.

## User experience

- A master switch enables or disables the preference.
- A country picker lists supported content-preference countries.
- Russia is excluded from the selectable list, as requested for this project.
- The selected country is shown with its flag, localized country name, and ISO 3166-1 alpha-2 code.
- The UI must label this as a content preference, not as SIM spoofing or a guaranteed TikTok feed-region change.
- VPN remains an independently configured system/app connection. MaterialTok must not silently enable, configure, or claim to verify a VPN.

## Initial country list

| Country | Code |
|---|---|
| Ukraine | UA |
| Netherlands | NL |
| United States | US |
| United Kingdom | GB |
| Germany | DE |
| France | FR |
| Spain | ES |
| Italy | IT |
| Sweden | SE |
| Poland | PL |

Operator names and MCC/MNC values are intentionally omitted from the picker: they describe real mobile networks and must not be presented as if selecting a country changes the user's SIM identity.

## Acceptance criteria

1. The setting is opt-in and persists locally.
2. The picker only accepts countries in the supported list above.
3. Russia cannot be selected.
4. The UI clearly explains that the setting is a preference only and cannot guarantee a different TikTok recommendation region.
5. No code attempts to falsify telephony identifiers, intercept TikTok traffic, or bypass platform region checks.
