---
paths:
  - 'flake.nix'
  - '.nix/**/*.nix'
  - '.nix/**/*.sh'
---

# Nix Development Environment

## File Organization

- `flake.nix` - main configuration (inputs, outputs, devShell)
- `.nix/pkgs/*.nix` - custom package definitions
- `.nix/apps/*.sh` - utility shell scripts

## Adding Packages to devShell

- Add to `buildInputs` in `devShells.default`
- Use `pkgs.<package>` for nixpkgs packages
- Create custom package in `.nix/pkgs/` for external tools
- Add symlinks in `shellHook` for IDE integration

## Creating Custom Packages

- Place in `.nix/pkgs/<name>.nix`
- Use standard Nix patterns (fetchurl, stdenv.mkDerivation)
- Include meta section with description, homepage, license
- Reference from flake.nix: `pkgs.callPackage ./.nix/pkgs/<name>.nix { }`

## Adding Nix Apps

- Create shell script in `.nix/apps/<name>.sh`
- Add app definition in `apps` section of flake.nix
- Wrap with required PATH and environment variables
- Use consistent formatting (BOLD/CYAN output headers)

## Treefmt Configuration

- Modify `.nix/pkgs/treefmt.nix` for formatter settings
- Available formatters: google-java-format, prettier (markdown/JSON), nixfmt

## Best Practices

- Use `forAllSystems` for cross-platform support
- Keep package versions pinned (explicit versions, sha256)
- Run `nix flake check` to validate changes
- Run `nix flake update` to update dependencies
