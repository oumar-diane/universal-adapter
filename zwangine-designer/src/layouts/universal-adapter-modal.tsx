import { AboutModal } from '@patternfly/react-core';
import { FunctionComponent } from 'react';
import logo from '@/assets/zenithbloxLogo.jpg';
import { About } from '@/components/about/about.tsx';

export interface IAboutModal {
    handleCloseModal: () => void;
    isModalOpen: boolean;
}

export const UaAboutModal: FunctionComponent<IAboutModal> = ({ handleCloseModal, isModalOpen }) => {
    return (
        <AboutModal
            isOpen={isModalOpen}
            onClose={handleCloseModal}
            trademark="UA"
            brandImageSrc={logo}
            brandImageAlt="UA Logo"
            data-testid="about-modal"
            productName="Universal Adapter"
        >
            <About />
        </AboutModal>
    );
};
