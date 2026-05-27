declare const google: {
    accounts: {
        id: {
            initialize(config: { client_id: string; callback: (response: { credential: string }) => void }): void;
            renderButton(el: HTMLElement, opts: object): void;
        };
    };
};
