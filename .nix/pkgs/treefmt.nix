{ pkgs, ... }:
{
  projectRootFile = "flake.nix";

  programs = {
    # Java formatting
    google-java-format = {
      enable = true;
    };

    # Markdown, JSON formatting
    prettier = {
      enable = true;
      settings = {
        trailingComma = "es5";
        semi = true;
        singleQuote = true;
        bracketSpacing = false;
      };
    };

    # Nix formatting
    nixfmt.enable = true;
  };
}
