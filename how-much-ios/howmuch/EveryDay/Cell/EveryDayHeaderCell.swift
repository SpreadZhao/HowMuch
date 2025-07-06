//
//  EveryDayHeaderCell.swift
//  howmuch
//
//  Created by ljx on 2025/7/6.
//

import IGListKit
import Combine

final class EveryDayHeaderCell: UICollectionViewCell, ListBindable {
    
    private var viewModel: EveryDayHeaderCellViewModel?
    private var cancellables = [AnyCancellable]()
    
    private lazy var dateLabel = {
        return UILabel(frame: .zero)
    }()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupSubviews()
        setupLayouts()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func prepareForReuse() {
        super.prepareForReuse()
        cancellables.removeAll()
    }
    
    func bindViewModel(_ viewModel: Any) {
        guard let viewModel = viewModel as? EveryDayHeaderCellViewModel else {
            return
        }
        self.viewModel = viewModel
        bind(viewModel: viewModel)
    }
    
    func bind(viewModel: EveryDayHeaderCellViewModel) {
        viewModel.$dateString.receive(on: RunLoop.main)
            .sink(receiveValue: { [weak self] in
                self?.dateLabel.text = $0
            })
            .store(in: &cancellables)
    }
    
    func setupSubviews() {
        contentView.addSubview(dateLabel)
    }
    
    func setupLayouts() {
        dateLabel.snp.makeConstraints { make in
            make.edges.equalToSuperview()
        }
    }
}
