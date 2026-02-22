{
  description = "oakgit development environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    treefmt-nix.url = "github:numtide/treefmt-nix";
  };

  outputs =
    {
      self,
      nixpkgs,
      treefmt-nix,
    }:
    let
      supportedSystems = [
        "x86_64-linux"
        "aarch64-linux"
        "x86_64-darwin"
        "aarch64-darwin"
      ];
      forAllSystems = nixpkgs.lib.genAttrs supportedSystems;
      treefmtEval =
        system: treefmt-nix.lib.evalModule nixpkgs.legacyPackages.${system} ./.nix/pkgs/treefmt.nix;
    in
    {
      formatter = forAllSystems (system: (treefmtEval system).config.build.wrapper);

      checks = forAllSystems (system: {
        formatting = (treefmtEval system).config.build.check self;
      });

      apps = forAllSystems (
        system:
        let
          pkgs = nixpkgs.legacyPackages.${system};
          maven4 = pkgs.callPackage ./.nix/pkgs/maven.nix { };
        in
        {
          update-deps = {
            type = "app";
            program = toString (
              pkgs.writeShellScript "update-deps-wrapper" ''
                export PATH="${maven4}/bin:${pkgs.jdk21}/bin:${pkgs.findutils}/bin:${pkgs.gnugrep}/bin:${pkgs.gnused}/bin:${pkgs.gawk}/bin:$PATH"
                export JAVA_HOME="${pkgs.jdk21}"
                exec ${pkgs.bash}/bin/bash ${./.nix/apps/update-deps.sh}
              ''
            );
          };
          clean = {
            type = "app";
            program = toString (
              pkgs.writeShellScript "clean-wrapper" ''
                export PATH="${maven4}/bin:${pkgs.jdk21}/bin:${pkgs.findutils}/bin:$PATH"
                export JAVA_HOME="${pkgs.jdk21}"
                exec ${pkgs.bash}/bin/bash ${./.nix/apps/clean.sh}
              ''
            );
          };
          sync-aem-sdk = {
            type = "app";
            program = toString (
              pkgs.writeShellScript "sync-aem-sdk-wrapper" ''
                export PATH="${maven4}/bin:${pkgs.jdk21}/bin:${pkgs.findutils}/bin:${pkgs.gnugrep}/bin:${pkgs.gnused}/bin:${pkgs.gawk}/bin:${pkgs.unzip}/bin:${pkgs.coreutils}/bin:$PATH"
                export JAVA_HOME="${pkgs.jdk21}"
                exec ${pkgs.bash}/bin/bash ${./.nix/apps/sync-aem-sdk.sh}
              ''
            );
          };
          localdeploy = {
            type = "app";
            program = toString (
              pkgs.writeShellScript "localdeploy-wrapper" ''
                export PATH="${maven4}/bin:${pkgs.jdk21}/bin:${pkgs.findutils}/bin:${pkgs.unzip}/bin:${pkgs.coreutils}/bin:$PATH"
                export JAVA_HOME="${pkgs.jdk21}"
                exec ${pkgs.bash}/bin/bash ${./.nix/apps/localdeploy.sh}
              ''
            );
          };
          localrun = {
            type = "app";
            program = toString (
              pkgs.writeShellScript "localrun-wrapper" ''
                export PATH="${pkgs.jdk21}/bin:${pkgs.coreutils}/bin:${pkgs.procps}/bin:$PATH"
                export JAVA_HOME="${pkgs.jdk21}"
                exec ${pkgs.bash}/bin/bash ${./.nix/apps/localrun.sh}
              ''
            );
          };
        }
      );

      devShells = forAllSystems (
        system:
        let
          pkgs = nixpkgs.legacyPackages.${system};
          maven4 = pkgs.callPackage ./.nix/pkgs/maven.nix { };
          treefmt = (treefmtEval system).config.build.wrapper;
        in
        {
          default = pkgs.mkShell {
            buildInputs = [
              pkgs.jdk21
              maven4
              treefmt
            ];

            shellHook = ''
              # Create stable symlinks for IDE integration
              mkdir -p .nix/tools
              ln -sfn ${maven4} .nix/tools/maven
              ln -sfn ${pkgs.jdk21} .nix/tools/jdk

              # Set up git hooks using treefmt
              if [ -d .git ]; then
                mkdir -p .git/hooks
                cat > .git/hooks/pre-commit << 'HOOK'
              #!/usr/bin/env bash
              # Format staged files with treefmt
              treefmt --fail-on-change
              HOOK
                chmod +x .git/hooks/pre-commit
              fi

              echo "oakgit dev environment"
              echo "Java:    $(java --version | head -1)"
              echo "Maven:   $(mvn --version | head -1)"
            '';
          };
        }
      );
    };
}
